import argparse
import os
import re
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

PILOTE = (
    REPO_ROOT / "reference" / "config" / "S-AWS-101_modl_pilote.csv"
)
JAVA_DIR = REPO_ROOT / "java"
GRADLEW = JAVA_DIR / ("gradlew.bat" if os.name == "nt" else "gradlew")

# Gradle application plugin installDist layout:
# java/build/install/<applicationName>/bin/<applicationName>.bat
# Here, settings.gradle sets rootProject.name = "format101-java"
INSTALLDIST_LAUNCHER = (
    JAVA_DIR
    / "build"
    / "install"
    / "format101-java"
    / "bin"
    / ("format101-java.bat" if os.name == "nt" else "format101-java")
)

ID_RE = re.compile(r"_id([A-Za-z0-9]{1,7})$")


def extract_identifier(stem: str) -> str:
    m = ID_RE.search(stem)
    if not m:
        raise ValueError(
            f"Cannot extract identifier from filename stem '{stem}'. "
            "Expected pattern '*_id<STATIONID>.format_101.txt' where STATIONID is 1..7 chars."
        )
    return m.group(1)


def validate_obs(obs_bytes: bytes) -> bool:
    n = len(obs_bytes)
    if not (10 <= n <= 70):
        return False
    return not any(0x20 <= b <= 0x3F for b in obs_bytes[10:])


def hex_tail_bytes(b: bytes, n: int = 16) -> str:
    t = b[-n:] if len(b) >= n else b
    return " ".join(f"{x:02x}" for x in t)


def load_exclusions(path: Path) -> dict[str, str]:
    if not path.exists():
        return {}
    exclusions = {}
    for line_number, raw in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if "|" not in line:
            raise ValueError(f"Invalid exclusion at {path}:{line_number}; expected STEM | REASON")
        stem, reason = (part.strip() for part in line.split("|", 1))
        if not stem or not reason or stem in exclusions:
            raise ValueError(f"Invalid exclusion at {path}:{line_number}")
        exclusions[stem] = reason
    return exclusions


def script_command(script: Path, *args: str) -> list[str]:
    if os.name == "nt":
        return ["cmd", "/c", str(script), *args]
    return [str(script), *args]


def run_install_dist_build() -> None:
    if not GRADLEW.exists():
        raise RuntimeError(f"Missing Gradle wrapper: {GRADLEW}")

    cmd = script_command(GRADLEW, "--no-daemon", "installDist")
    proc = subprocess.run(
        cmd,
        cwd=JAVA_DIR,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    if proc.returncode != 0:
        out = proc.stdout.decode("utf-8", errors="replace")
        raise RuntimeError(f"installDist failed (exit={proc.returncode}):\n{out}")


def run_java_install_dist(pilote: Path, format101: Path, station_id: str) -> bytes:
    if not INSTALLDIST_LAUNCHER.exists():
        raise FileNotFoundError(f"Missing installDist launcher: {INSTALLDIST_LAUNCHER}")

    cmd = script_command(
        INSTALLDIST_LAUNCHER,
        "encode",
        "--pilote",
        str(pilote),
        "--format101",
        str(format101),
        "--station-id",
        station_id,
    )
    proc = subprocess.run(
        cmd,
        cwd=REPO_ROOT,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    if proc.returncode != 0:
        raise RuntimeError(
            f"java installDist runner failed (exit={proc.returncode}):\n"
            + proc.stdout.decode("utf-8", errors="replace")
        )
    return proc.stdout


def run_java_via_gradle(pilote: Path, format101: Path, station_id: str) -> bytes:
    if not GRADLEW.exists():
        raise RuntimeError(f"Missing Gradle wrapper: {GRADLEW}")

    cmd = script_command(
        GRADLEW,
        "--no-daemon",
        "run",
        "--quiet",
        "--args",
        f"encode --pilote {pilote} --format101 {format101} --station-id {station_id}",
    )
    proc = subprocess.run(
        cmd,
        cwd=JAVA_DIR,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    if proc.returncode != 0:
        raise RuntimeError(
            f"java gradle runner failed (exit={proc.returncode}):\n"
            + proc.stdout.decode("utf-8", errors="replace")
        )
    return proc.stdout


def extract_last_line(stdout_bytes: bytes) -> bytes:
    """
    Return the last output line without changing payload bytes.
    """
    lines = stdout_bytes.split(b"\n")
    while lines and lines[-1] == b"":
        lines.pop()
    return lines[-1].removesuffix(b"\r") if lines else b""


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dir",
        type=Path,
        default=REPO_ROOT / "tests" / "vectors",
        help="Directory containing *.format_101.txt vectors (default: tests/vectors)",
    )
    ap.add_argument(
        "--build",
        action="store_true",
        help="Run the platform's Gradle wrapper with --no-daemon installDist before verifying",
    )
    ap.add_argument(
        "--prefer-gradle",
        action="store_true",
        help="Prefer running via 'gradlew run' even if installDist launcher exists (slow)",
    )
    ap.add_argument(
        "--exclusions",
        type=Path,
        help="Explicit exclusion manifest (default: <vector-dir>/exclusions.txt)",
    )
    args = ap.parse_args()

    vectors_dir: Path = args.dir.resolve()
    exclusions = load_exclusions(args.exclusions or vectors_dir / "exclusions.txt")
    vectors = sorted(vectors_dir.glob("*.format_101.txt"))
    passed = failed = excluded = 0

    if not vectors:
        print(f"[FAIL] No format_101 vectors found in {vectors_dir}")

    if args.build:
        run_install_dist_build()

    use_install_dist = INSTALLDIST_LAUNCHER.exists() and not args.prefer_gradle
    if not use_install_dist:
        if not GRADLEW.exists():
            raise RuntimeError("Neither installDist launcher nor Gradle wrapper found")

    for input_path in vectors:
        stem = input_path.stem.replace(".format_101", "")
        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            failed += 1
            continue

        station_id = extract_identifier(stem)

        exp = extract_last_line(expected_path.read_bytes())
        if not validate_obs(exp):
            reason = exclusions.get(stem)
            if reason is None:
                print(f"[FAIL] {stem} (invalid reference output; add an explicit exclusion with a reason)")
                failed += 1
            else:
                print(f"[EXCLUDED] {stem}: {reason}")
                excluded += 1
            continue

        try:
            if use_install_dist:
                stdout_bytes = run_java_install_dist(PILOTE, input_path, station_id)
            else:
                stdout_bytes = run_java_via_gradle(PILOTE, input_path, station_id)
        except Exception as e:
            failed += 1
            print(f"[FAIL] {stem} ({e})")
            continue

        got_line_bytes = extract_last_line(stdout_bytes)

        if got_line_bytes != exp:
            failed += 1
            print(f"[FAIL] {stem}")
            print(f"  station_id: {station_id!r}")
            print(f"  expected  : {exp.decode('latin1', errors='replace')!r}")
            print(f"  got       : {got_line_bytes.decode('latin1', errors='replace')!r}")
            print(f"  exp_len   : {len(exp)}")
            print(f"  got_len   : {len(got_line_bytes)}")
            print(f"  exp_tail16: {hex_tail_bytes(exp)}")
            print(f"  got_tail16: {hex_tail_bytes(got_line_bytes)}")
        else:
            passed += 1
            print(f"[OK]   {stem}")

    print(f"Summary: passed={passed} failed={failed} excluded={excluded}")
    return 0 if failed == 0 and passed > 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())

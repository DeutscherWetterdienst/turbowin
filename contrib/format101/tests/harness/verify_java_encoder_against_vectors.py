import argparse
import re
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)
JAVA_DIR = REPO_ROOT / "java"
GRADLEW = JAVA_DIR / "gradlew.bat"

# Gradle application plugin installDist layout:
# java/build/install/<applicationName>/bin/<applicationName>.bat
# Here, settings.gradle sets rootProject.name = "format101-java"
INSTALLDIST_BAT = (
    JAVA_DIR / "build" / "install" / "format101-java" / "bin" / "format101-java.bat"
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


def run_install_dist_build() -> None:
    if not GRADLEW.exists():
        raise RuntimeError(f"Missing Gradle wrapper: {GRADLEW}")

    cmd = [
        "cmd",
        "/c",
        str(GRADLEW),
        "--no-daemon",
        "installDist",
    ]
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
    if not INSTALLDIST_BAT.exists():
        raise FileNotFoundError(f"Missing installDist launcher: {INSTALLDIST_BAT}")

    cmd = [
        "cmd",
        "/c",
        str(INSTALLDIST_BAT),
        "encode",
        "--pilote",
        str(pilote),
        "--format101",
        str(format101),
        "--station-id",
        station_id,
    ]
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

    cmd = [
        "cmd",
        "/c",
        str(GRADLEW),
        "--no-daemon",
        "run",
        "--quiet",
        "--args",
        f"encode --pilote {pilote} --format101 {format101} --station-id {station_id}",
    ]
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


def extract_last_line_as_latin1(stdout_bytes: bytes) -> tuple[str, bytes]:
    """
    Return (decoded_line, raw_line_bytes) from process stdout.

    We avoid text=True / encoding=... here to be robust against Windows code pages.
    """
    lines = stdout_bytes.splitlines()
    last = lines[-1] if lines else b""
    return last.decode("latin1", errors="replace"), last


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
        help="Run 'gradlew.bat --no-daemon installDist' before verifying (recommended after Java code changes)",
    )
    ap.add_argument(
        "--prefer-gradle",
        action="store_true",
        help="Prefer running via 'gradlew run' even if installDist launcher exists (slow)",
    )
    args = ap.parse_args()

    vectors_dir: Path = args.dir
    ok = True

    if args.build:
        run_install_dist_build()

    use_install_dist = INSTALLDIST_BAT.exists() and not args.prefer_gradle
    if not use_install_dist:
        if not GRADLEW.exists():
            raise RuntimeError("Neither installDist launcher nor Gradle wrapper found")

    for input_path in sorted(vectors_dir.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            ok = False
            continue

        station_id = extract_identifier(stem)

        exp = expected_path.read_bytes().splitlines()[0]
        if not validate_obs(exp):
            print(f"[SKIP] {stem} (expected output would be rejected by validate_obs)")
            continue

        try:
            if use_install_dist:
                stdout_bytes = run_java_install_dist(PILOTE, input_path, station_id)
            else:
                stdout_bytes = run_java_via_gradle(PILOTE, input_path, station_id)
        except Exception as e:
            ok = False
            print(f"[FAIL] {stem} ({e})")
            continue

        got_line, got_line_bytes = extract_last_line_as_latin1(stdout_bytes)

        exp_line = exp.decode("latin1", errors="replace")
        if got_line != exp_line:
            ok = False
            print(f"[FAIL] {stem}")
            print(f"  station_id: {station_id!r}")
            print(f"  expected  : {exp_line!r}")
            print(f"  got       : {got_line!r}")
            print(f"  exp_len   : {len(exp)}")
            print(f"  got_len   : {len(got_line_bytes)}")
            print(f"  exp_tail16: {hex_tail_bytes(exp)}")
            print(f"  got_tail16: {hex_tail_bytes(got_line_bytes)}")
        else:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

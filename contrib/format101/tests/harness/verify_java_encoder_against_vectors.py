import argparse
import re
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

DEFAULT_VECTORS_DIR = REPO_ROOT / "tests" / "vectors"
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)
JAVA_DIR = REPO_ROOT / "java"

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


def hex_tail(s: str, n: int = 16) -> str:
    b = s.encode("latin1")
    t = b[-n:] if len(b) >= n else b
    return " ".join(f"{x:02x}" for x in t)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dir",
        type=Path,
        default=DEFAULT_VECTORS_DIR,
        help="Directory containing *.format_101.txt vectors (default: tests/vectors)",
    )
    ap.add_argument(
        "--pilote",
        type=Path,
        default=DEFAULT_PILOTE,
        help="Pilot CSV to use (default: TurboWin+ legacy copy)",
    )
    args = ap.parse_args()

    vectors_dir: Path = args.dir
    pilote: Path = args.pilote

    ok = True

    for input_path in sorted(vectors_dir.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            ok = False
            continue

        station_id = extract_identifier(stem)
        exp = expected_path.read_text(encoding="latin1").splitlines()[0]

        if not validate_obs(exp.encode("latin1")):
            print(f"[SKIP] {stem} (expected output would be rejected by validate_obs)")
            continue

        cmd = [
            "cmd",
            "/c",
            "gradlew.bat",
            "--no-daemon",
            "run",
            "--quiet",
            "--args",
            f"encode --pilote {pilote} --format101 {input_path} --station-id {station_id}",
        ]
        proc = subprocess.run(
            cmd,
            cwd=JAVA_DIR,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="latin1",
            check=False,
        )
        out = proc.stdout.splitlines()
        got = out[-1] if out else ""

        if proc.returncode != 0:
            ok = False
            print(f"[FAIL] {stem} (java exit={proc.returncode})")
            print(proc.stdout)
            continue

        if got != exp:
            ok = False
            print(f"[FAIL] {stem}")
            print(f"  station_id: {station_id!r}")
            print(f"  expected  : {exp!r}")
            print(f"  got       : {got!r}")
            print(f"  exp_len   : {len(exp)}")
            print(f"  got_len   : {len(got)}")
            print(f"  exp_tail16: {hex_tail(exp)}")
            print(f"  got_tail16: {hex_tail(got)}")
            print("\n  --- java output ---")
            print(proc.stdout)
        else:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

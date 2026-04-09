import re
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

VECTORS_DIR = REPO_ROOT / "tests" / "vectors"
PILOTE = (
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


def main() -> int:
    ok = True

    for input_path in sorted(VECTORS_DIR.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        expected_path = VECTORS_DIR / f"{stem}.expected.hpk.txt"
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
            "run",
            "--quiet",
            "--args",
            f"encode --pilote {PILOTE} --format101 {input_path} --station-id {station_id}",
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
        else:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

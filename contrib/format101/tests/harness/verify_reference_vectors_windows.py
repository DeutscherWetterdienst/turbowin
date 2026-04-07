import argparse
import re
from pathlib import Path

from run_reference_encoder_windows import REPO_ROOT, run_one

DEFAULT_VECTORS_DIR = REPO_ROOT / "tests" / "vectors"

ID_RE = re.compile(r"_id([A-Za-z0-9]{1,7})$")


def extract_identifier(stem: str) -> str:
    m = ID_RE.search(stem)
    if not m:
        raise ValueError(
            f"Cannot extract identifier from filename stem '{stem}'. "
            "Expected pattern '*_id<STATIONID>.format_101.txt' where STATIONID is 1..7 chars."
        )
    return m.group(1)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dir",
        type=Path,
        default=DEFAULT_VECTORS_DIR,
        help="Directory containing vectors (default: tests/vectors)",
    )
    args = ap.parse_args()

    vectors_dir = args.dir
    work_dir = REPO_ROOT / "tests" / ".work"
    work_dir.mkdir(parents=True, exist_ok=True)

    ok = True

    for input_path in sorted(vectors_dir.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            ok = False
            continue

        identifier = extract_identifier(stem)
        got = run_one(input_path, identifier).rstrip("\n")
        exp = expected_path.read_text(encoding="latin1").splitlines()[0]

        if got != exp:
            ok = False
            print(f"[FAIL] {stem}")
            print(f"  identifier: {identifier!r}")
            print(f"  expected  : {exp!r}")
            print(f"  got       : {got!r}")
        else:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

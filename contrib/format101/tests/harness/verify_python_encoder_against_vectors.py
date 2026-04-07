import argparse
import re
from pathlib import Path

from format101.encoder import encode_format101_from_txt

REPO_ROOT = Path(__file__).resolve().parents[2]

DEFAULT_VECTORS_DIR = REPO_ROOT / "tests" / "vectors"
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
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


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dir",
        type=Path,
        default=DEFAULT_VECTORS_DIR,
        help="Directory containing vectors (default: tests/vectors)",
    )
    args = ap.parse_args()

    ok = True
    vectors_dir = args.dir

    for input_path in sorted(vectors_dir.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            ok = False
            continue

        station_id = extract_identifier(stem)

        msg = encode_format101_from_txt(
            format101_txt=input_path,
            pilote_csv=DEFAULT_PILOTE,
            station_id=station_id,
        )
        got = msg.to_hpk_line()
        exp = expected_path.read_text(encoding="latin1").splitlines()[0]

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

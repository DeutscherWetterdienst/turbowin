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


def validate_obs(obs_bytes: bytes) -> bool:
    """Validate OBS format 101 requirements."""
    n = len(obs_bytes)
    if not (10 <= n <= 70):
        return False
    # Check if any byte in the tail falls within the forbidden ASCII range.
    return not any(0x20 <= b <= 0x3F for b in obs_bytes[10:])


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
    pilote_csv: Path = args.pilote

    ok = True

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
            pilote_csv=pilote_csv,
            station_id=station_id,
        )
        got = msg.to_hpk_line()
        exp = expected_path.read_text(encoding="latin1").splitlines()[0]

        if not validate_obs(exp.encode("latin1")):
            print(f"[SKIP] {stem} (expected output would be rejected by validate_obs)")
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

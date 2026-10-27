import argparse
import re
from pathlib import Path

from format101.encoder import encode_format101_from_txt

REPO_ROOT = Path(__file__).resolve().parents[2]

DEFAULT_VECTORS_DIR = REPO_ROOT / "tests" / "vectors"
DEFAULT_PILOTE = (
    REPO_ROOT / "reference" / "config" / "S-AWS-101_modl_pilote.csv"
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
    ap.add_argument(
        "--exclusions",
        type=Path,
        help="Explicit exclusion manifest (default: <vector-dir>/exclusions.txt)",
    )
    args = ap.parse_args()

    vectors_dir: Path = args.dir
    pilote_csv: Path = args.pilote
    exclusions = load_exclusions(args.exclusions or vectors_dir / "exclusions.txt")

    vectors = sorted(vectors_dir.glob("*.format_101.txt"))
    passed = failed = excluded = 0

    if not vectors:
        print(f"[FAIL] No format_101 vectors found in {vectors_dir}")

    for input_path in vectors:
        stem = input_path.stem.replace(".format_101", "")
        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            failed += 1
            continue

        station_id = extract_identifier(stem)

        try:
            msg = encode_format101_from_txt(
                format101_txt=input_path,
                pilote_csv=pilote_csv,
                station_id=station_id,
            )
        except Exception as exc:
            print(f"[FAIL] {stem} ({exc})")
            failed += 1
            continue
        got = msg.to_hpk_line()
        exp = expected_path.read_text(encoding="latin1").splitlines()[0]

        if not validate_obs(exp.encode("latin1")):
            reason = exclusions.get(stem)
            if reason is None:
                print(f"[FAIL] {stem} (invalid reference output; add an explicit exclusion with a reason)")
                failed += 1
            else:
                print(f"[EXCLUDED] {stem}: {reason}")
                excluded += 1
            continue

        if got != exp:
            failed += 1
            print(f"[FAIL] {stem}")
            print(f"  station_id: {station_id!r}")
            print(f"  expected  : {exp!r}")
            print(f"  got       : {got!r}")
        else:
            passed += 1
            print(f"[OK]   {stem}")

    print(f"Summary: passed={passed} failed={failed} excluded={excluded}")
    return 0 if failed == 0 and passed > 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())

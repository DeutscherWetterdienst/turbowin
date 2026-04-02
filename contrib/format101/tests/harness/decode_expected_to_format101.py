import argparse
from pathlib import Path

from format101.decoder import decode_hpk_line


REPO_ROOT = Path(__file__).resolve().parents[2]


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--pilote",
        default=str(
            REPO_ROOT
            / "miscellaneous"
            / "format_101"
            / "config"
            / "S-AWS-101_modl_pilote.csv"
        ),
        help="Path to pilot CSV (default: TurboWin+ legacy copy)",
    )
    ap.add_argument(
        "expected_hpk",
        type=Path,
        help="Path to *.expected.hpk.txt (first line is decoded)",
    )
    ap.add_argument(
        "--out",
        type=Path,
        required=True,
        help="Write decoded format_101.txt to this path",
    )
    args = ap.parse_args()

    hpk_line = args.expected_hpk.read_text(encoding="latin1").splitlines()[0]
    msg = decode_hpk_line(hpk_line, pilote_csv=args.pilote)
    args.out.write_text(msg.to_format101_txt(), encoding="utf-8")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

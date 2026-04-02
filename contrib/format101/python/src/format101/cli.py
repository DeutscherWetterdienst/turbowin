import argparse
from pathlib import Path

from format101.decoder import decode_hpk_line
from format101.encoder import encode_format101_from_txt


def main() -> int:
    ap = argparse.ArgumentParser(prog="format101")
    sub = ap.add_subparsers(dest="cmd", required=False)

    dec = sub.add_parser("decode", help="Decode a TurboWin+ HPK format_101 line")
    dec.add_argument(
        "--pilote",
        default="miscellaneous/format_101/config/S-AWS-101_modl_pilote.csv",
        help="Path to pilot CSV (default: TurboWin+ legacy copy)",
    )
    dec.add_argument(
        "--hpk-file",
        type=Path,
        help="Read the HPK line from a file (first line)",
    )
    dec.add_argument(
        "--hpk",
        help="Provide the HPK line directly",
    )
    dec.add_argument(
        "--out-format101",
        type=Path,
        help="Write canonical format_101.txt style output to this file",
    )

    enc = sub.add_parser(
        "encode", help="Encode a TurboWin+ format_101.txt into an HPK line"
    )
    enc.add_argument(
        "--pilote",
        default="miscellaneous/format_101/config/S-AWS-101_modl_pilote.csv",
        help="Path to pilot CSV (default: TurboWin+ legacy copy)",
    )
    enc.add_argument(
        "--format101",
        type=Path,
        required=True,
        help="Path to format_101.txt input file",
    )
    enc.add_argument(
        "--station-id",
        required=True,
        help="Station ID / indicatif (1..7 chars in TurboWin+)",
    )

    args = ap.parse_args()

    if args.cmd is None:
        print("format101 CLI (WIP)")
        return 0

    if args.cmd == "decode":
        if args.hpk_file is None and args.hpk is None:
            dec.error("Either --hpk-file or --hpk must be provided")

        if args.hpk_file is not None and args.hpk is not None:
            dec.error("Only one of --hpk-file or --hpk can be provided")

        if args.hpk_file is not None:
            hpk_line = args.hpk_file.read_text(encoding="latin1").splitlines()[0]
        else:
            hpk_line = args.hpk

        msg = decode_hpk_line(hpk_line, pilote_csv=args.pilote)
        out_txt = msg.to_format101_txt()

        if args.out_format101:
            args.out_format101.write_text(out_txt, encoding="utf-8")
        else:
            print(out_txt, end="")

        return 0

    if args.cmd == "encode":
        msg = encode_format101_from_txt(
            format101_txt=args.format101,
            pilote_csv=args.pilote,
            station_id=args.station_id,
        )
        print(msg.to_hpk_line())
        return 0

    print("format101 CLI (WIP)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

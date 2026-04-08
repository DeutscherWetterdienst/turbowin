import argparse
from pathlib import Path

from format101.codec6 import decode_turbowin_text_to_octets
from format101.encoder import encode_format101_from_txt

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Compare expected vs Python encoder octet streams"
    )
    ap.add_argument("--dir", type=Path, required=True)
    ap.add_argument("--name", required=True)
    ap.add_argument("--station-id", required=True)
    ap.add_argument("--pilote", type=Path, default=DEFAULT_PILOTE)
    args = ap.parse_args()

    vec_dir = args.dir
    name = args.name
    station_id = args.station_id

    in_txt = vec_dir / f"{name}.format_101.txt"
    exp_hpk = vec_dir / f"{name}.expected.hpk.txt"

    exp_line = exp_hpk.read_text(encoding="latin1").splitlines()[0]
    exp_oct = decode_turbowin_text_to_octets(exp_line[7:].encode("latin1"))

    msg = encode_format101_from_txt(
        format101_txt=in_txt,
        pilote_csv=args.pilote,
        station_id=station_id,
    )
    got_oct = msg.payload_octets

    print(f"expected_octets_len={len(exp_oct)}")
    print(f"got_octets_len     ={len(got_oct)}")
    print(f"got_payload_bits   ={msg.payload_bits}")

    n = min(len(exp_oct), len(got_oct))
    first_diff = None
    for i in range(n):
        if exp_oct[i] != got_oct[i]:
            first_diff = i
            break

    if first_diff is None and len(exp_oct) == len(got_oct):
        print("octets match exactly")
        return 0

    if first_diff is None:
        print("octets match on common prefix, lengths differ")
        first_diff = n

    print(f"first_diff_index={first_diff}")

    def hex_window(buf: bytes, idx: int, before: int = 16, after: int = 16) -> str:
        start = max(0, idx - before)
        end = min(len(buf), idx + after)
        return " ".join(f"{b:02x}" for b in buf[start:end])

    print("\nexpected_hex_window:")
    print(hex_window(exp_oct, first_diff))
    print("\ngot_hex_window:")
    print(hex_window(got_oct, first_diff))

    if first_diff < len(exp_oct):
        print(f"\nexpected_byte={exp_oct[first_diff]:02x}")
    if first_diff < len(got_oct):
        print(f"got_byte     ={got_oct[first_diff]:02x}")

    return 1


if __name__ == "__main__":
    raise SystemExit(main())

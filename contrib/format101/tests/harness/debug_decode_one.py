import argparse
from pathlib import Path

from format101.codec6 import compact_6bit_text_to_octets, expand_6bit_text_to_octets
from format101.decoder import decode_hpk_line
from format101.spec import load_pilote_csv, pilote_key


REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("expected_hpk", type=Path, help="Path to *.expected.hpk.txt")
    ap.add_argument("--pilote", type=Path, default=DEFAULT_PILOTE)
    args = ap.parse_args()

    hpk_line = args.expected_hpk.read_text(encoding="latin1").splitlines()[0]
    raw = hpk_line.rstrip("\r\n")
    prefix = raw[:7]
    payload = raw[7:].encode("latin1")

    print(f"station_id_raw={prefix!r}")
    print(f"payload_len={len(payload)}")
    print("payload_hex_first_32:", " ".join(f"{b:02x}" for b in payload[:32]))

    octets_compact = compact_6bit_text_to_octets(payload)
    octets_expand = expand_6bit_text_to_octets(payload)

    print(f"octets_compact_len={len(octets_compact)}")
    print(
        "octets_compact_hex_first_16:",
        " ".join(f"{b:02x}" for b in octets_compact[:16]),
    )

    print(f"octets_expand_len={len(octets_expand)}")
    print(
        "octets_expand_hex_first_16:", " ".join(f"{b:02x}" for b in octets_expand[:16])
    )

    pilote = load_pilote_csv(args.pilote)
    if pilote and pilote[0].bufr == "000000" and pilote[0].ref == "":
        pilote = pilote[1:]

    from format101.bitstream import read_bits

    def decode_first(octets: bytes, label: str, count: int) -> None:
        b_ofs = 0
        print(f"\n== {label} ==")
        for idx, desc in enumerate(pilote[:count]):
            b_ofs, rawv = read_bits(octets, b_ofs, desc.nbits)
            key = pilote_key(desc)
            print(f"[{idx}] {key} nbits={desc.nbits} raw={rawv}")

    decode_first(octets_compact, "compact_6bit_text_to_octets (first 45 fields)", 45)
    decode_first(octets_expand, "expand_6bit_text_to_octets (first 45 fields)", 45)

    msg = decode_hpk_line(hpk_line, pilote_csv=args.pilote)
    print("\n== decode_hpk_line output (first 45 fields) ==")
    for f in msg.fields[:45]:
        print(f"{f.key} = {f.value!r}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

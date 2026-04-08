import argparse
from pathlib import Path

from format101.bitstream import read_bits
from format101.codec6 import decode_turbowin_text_to_octets
from format101.decoder import decode_hpk_line
from format101.encoder import encode_format101_from_txt
from format101.spec import load_pilote_csv, pilote_key

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)


def to_payload_octets_from_hpk_line(hpk_line: str) -> bytes:
    raw = hpk_line.rstrip("\r\n")
    payload_text = raw[7:].encode("latin1")
    return decode_turbowin_text_to_octets(payload_text)


def dump_first_diff(
    exp_octets: bytes,
    got_octets: bytes,
    pilote_path: Path,
) -> int:
    pilote = load_pilote_csv(pilote_path)
    if pilote and pilote[0].bufr == "000000" and pilote[0].ref == "":
        pilote = pilote[1:]

    exp_bits = len(exp_octets) * 8
    got_bits = len(got_octets) * 8

    print(f"expected_octets_len={len(exp_octets)} expected_bits={exp_bits}")
    print(f"got_octets_len={len(got_octets)} got_bits={got_bits}")

    b_exp = 0
    b_got = 0

    for idx, entry in enumerate(pilote):
        key = pilote_key(entry)
        n = entry.nbits

        have_exp = b_exp + n <= exp_bits
        have_got = b_got + n <= got_bits

        if not have_exp or not have_got:
            print("\n== LENGTH / STRUCTURE DIFFERENCE ==")
            print(f"field_idx={idx} key={key} nbits={n}")
            print(f"have_expected={have_exp} have_got={have_got}")
            print(f"expected_bitpos={b_exp} got_bitpos={b_got}")
            print("This indicates a structural/length mismatch")
            return 2

        treat_missing = True
        if entry.bufr in ("410000", "408000") and entry.nbits == 1:
            treat_missing = False

        b_exp2, raw_exp = read_bits(
            exp_octets, b_exp, n, treat_all_ones_as_missing=treat_missing
        )
        b_got2, raw_got = read_bits(
            got_octets, b_got, n, treat_all_ones_as_missing=treat_missing
        )

        if raw_exp != raw_got:
            print("\n== FIRST RAW FIELD DIFFERENCE ==")
            print(f"field_idx={idx} key={key} nbits={n}")
            print(f"expected_raw={raw_exp!r}")
            print(f"got_raw     ={raw_got!r}")
            print(f"expected_bitpos={b_exp} got_bitpos={b_got}")
            return 1

        b_exp = b_exp2
        b_got = b_got2

    print("\nNo differences found within pilote fields")
    if exp_bits != got_bits:
        print(
            f"Note: total length differs after last field (expected_bits={exp_bits}, got_bits={got_bits})"
        )
        return 3
    return 0


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Compare reference (expected) vs Python-encoded payload at the bit/field level"
    )
    ap.add_argument(
        "--dir",
        type=Path,
        required=True,
        help="Directory containing *.format_101.txt and *.expected.hpk.txt",
    )
    ap.add_argument(
        "--name",
        required=True,
        help="Vector base name without extension",
    )
    ap.add_argument(
        "--station-id",
        required=True,
        help="Station ID used for Python encoding (1..7 chars)",
    )
    ap.add_argument(
        "--pilote",
        type=Path,
        default=DEFAULT_PILOTE,
        help="Pilot CSV to use (default: TurboWin+ legacy copy)",
    )
    args = ap.parse_args()

    vec_dir = args.dir
    name = args.name

    in_txt = vec_dir / f"{name}.format_101.txt"
    exp_hpk = vec_dir / f"{name}.expected.hpk.txt"

    if not in_txt.exists():
        raise SystemExit(f"Missing input file: {in_txt}")
    if not exp_hpk.exists():
        raise SystemExit(f"Missing expected file: {exp_hpk}")

    exp_line = exp_hpk.read_text(encoding="latin1").splitlines()[0]
    exp_octets = to_payload_octets_from_hpk_line(exp_line)

    msg = encode_format101_from_txt(
        format101_txt=in_txt,
        pilote_csv=args.pilote,
        station_id=args.station_id,
    )
    got_octets = msg.payload_octets

    rc = dump_first_diff(exp_octets, got_octets, args.pilote)

    try:
        exp_dec = decode_hpk_line(exp_line, pilote_csv=args.pilote)
        got_dec = decode_hpk_line(msg.to_hpk_line(), pilote_csv=args.pilote)
        exp_map = {f.key: f.value for f in exp_dec.fields}
        got_map = {f.key: f.value for f in got_dec.fields}
        for k in ("410000_visual", "408000_wave", "408000_ice"):
            if k in exp_map or k in got_map:
                print(
                    f"\nmarker {k}: expected={exp_map.get(k)!r} got={got_map.get(k)!r}"
                )
    except Exception as e:
        print(f"\n(note) decode_hpk_line failed during context decode: {e}")

    return rc


if __name__ == "__main__":
    raise SystemExit(main())

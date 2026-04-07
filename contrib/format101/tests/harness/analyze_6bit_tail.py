import argparse
import itertools
from pathlib import Path

from format101.codec6 import decode_turbowin_text_to_octets
from format101.encoder import encode_format101_from_txt

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)


def six_from_text(text: bytes) -> bytes:
    """
    Convert TurboWin payload text bytes (0x40..0x7F) to 6-bit bytes (0x00..0x3F).
    """
    return bytes(((b - 0x40) & 0x3F) for b in text)


def text_from_six(six: bytes) -> bytes:
    """
    Convert 6-bit bytes (0x00..0x3F) to TurboWin payload text bytes (0x40..0x7F).
    """
    return bytes(((b & 0x3F) + 0x40) for b in six)


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Analyze tail/padding differences between reference and Python 8->6 encoding"
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
        help="Vector base name without extension (e.g. generated_combo_v0w0i0_01_idA)",
    )
    ap.add_argument(
        "--station-id",
        required=True,
        help="Station ID used for encoding (1..7 chars)",
    )
    ap.add_argument(
        "--max-tail",
        type=int,
        default=3,
        help="Max number of 6-bit tail characters to brute-force (default: 3)",
    )
    ap.add_argument(
        "--alphabet",
        default="all",
        choices=["all", "printable"],
        help="Search alphabet for tail chars: all=0..63, printable=0x20..0x3F",
    )
    args = ap.parse_args()

    vec_dir = args.dir
    name = args.name
    station_id = args.station_id

    in_txt = vec_dir / f"{name}.format_101.txt"
    exp_hpk = vec_dir / f"{name}.expected.hpk.txt"

    if not in_txt.exists():
        raise SystemExit(f"Missing input file: {in_txt}")
    if not exp_hpk.exists():
        raise SystemExit(f"Missing expected file: {exp_hpk}")

    exp_line = exp_hpk.read_text(encoding="latin1").splitlines()[0]
    exp_raw = exp_line.rstrip("\r\n")
    if len(exp_raw) < 7:
        raise SystemExit("Expected HPK line too short")

    exp_prefix = exp_raw[:7]
    exp_payload_text = exp_raw[7:].encode("latin1")

    print(f"expected_prefix={exp_prefix!r}")
    print(f"expected_payload_len={len(exp_payload_text)}")

    # Reference payload octets (decoded from expected)
    exp_octets = decode_turbowin_text_to_octets(exp_payload_text)
    print(f"expected_octets_len={len(exp_octets)}")
    print(
        "expected_payload_hex_last_16:",
        " ".join(f"{b:02x}" for b in exp_payload_text[-16:]),
    )
    print(
        "expected_octets_hex_last_16:", " ".join(f"{b:02x}" for b in exp_octets[-16:])
    )

    # Our encoder output (current implementation)
    msg = encode_format101_from_txt(
        format101_txt=in_txt,
        pilote_csv=DEFAULT_PILOTE,
        station_id=station_id,
    )
    got_line = msg.to_hpk_line()
    got_raw = got_line.rstrip("\r\n")
    got_prefix = got_raw[:7]
    got_payload_text = got_raw[7:].encode("latin1")

    got_octets = decode_turbowin_text_to_octets(got_payload_text)

    print(f"\ngot_prefix={got_prefix!r}")
    print(f"got_payload_len={len(got_payload_text)}")
    print(f"got_octets_len={len(got_octets)}")
    print(
        "got_payload_hex_last_16:", " ".join(f"{b:02x}" for b in got_payload_text[-16:])
    )
    print("got_octets_hex_last_16:", " ".join(f"{b:02x}" for b in got_octets[-16:]))

    # Quick sanity check: octets should match for most of the stream
    common = min(len(exp_octets), len(got_octets))
    prefix_match = 0
    for i in range(common):
        if exp_octets[i] != got_octets[i]:
            break
        prefix_match += 1

    print(f"\noctet_prefix_match={prefix_match}/{common}")

    # If octets are identical already, show only tail difference in text
    if exp_octets == got_octets:
        print("Octets match exactly; mismatch is only in text representation")
    else:
        print(
            "Octets differ; tail brute-force will attempt to adjust text tail to match expected octets"
        )

    # Brute-force tail adjustment: try to replace last N 6-bit words in got payload
    # so that decoding matches expected octets.
    if args.alphabet == "printable":
        alph = list(range(0x20, 0x40))
    else:
        alph = list(range(0x00, 0x40))

    got_six = six_from_text(got_payload_text)

    print(
        f"\nTrying tail brute-force (max_tail={args.max_tail}, alphabet={args.alphabet})"
    )

    for n_tail in range(1, args.max_tail + 1):
        if len(got_six) < n_tail:
            continue

        base = got_six[:-n_tail]
        target_octets = exp_octets

        for tail_tuple in itertools.product(alph, repeat=n_tail):
            cand_six = base + bytes(tail_tuple)
            cand_text = text_from_six(cand_six)
            cand_octets = decode_turbowin_text_to_octets(cand_text)
            if cand_octets == target_octets:
                print("\nFOUND tail fix!")
                print(f"n_tail={n_tail}")
                print("tail_6bit:", list(tail_tuple))
                print(
                    "tail_text_bytes:",
                    [hex(b) for b in text_from_six(bytes(tail_tuple))],
                )
                print(
                    "tail_text_str (latin1):",
                    text_from_six(bytes(tail_tuple)).decode("latin1", errors="replace"),
                )
                print(
                    "expected_tail_text_str :",
                    exp_payload_text[-n_tail:].decode("latin1", errors="replace"),
                )
                print(
                    "got_tail_text_str      :",
                    got_payload_text[-n_tail:].decode("latin1", errors="replace"),
                )
                return 0

        print(f"no solution for n_tail={n_tail}")

    print("\nNo tail fix found within the configured search space")
    print("Hint: increase --max-tail or use --alphabet all")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())

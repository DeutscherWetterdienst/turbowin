import argparse
from dataclasses import dataclass
from pathlib import Path

from format101.bitstream import write_bits
from format101.codec6 import decode_turbowin_text_to_octets
from format101.encoder import encode_format101_from_txt

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)


@dataclass(frozen=True)
class Solution:
    pad_bits: str
    final_bits: int
    final_octets_len: int


def load_expected_octets(expected_hpk: Path) -> bytes:
    line = expected_hpk.read_text(encoding="latin1").splitlines()[0]
    return decode_turbowin_text_to_octets(line[7:].encode("latin1"))


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Solve reference padding by brute forcing a small number of tail bits"
    )
    ap.add_argument("--dir", type=Path, required=True)
    ap.add_argument("--name", required=True)
    ap.add_argument("--station-id", required=True)
    ap.add_argument("--pilote", type=Path, default=DEFAULT_PILOTE)
    ap.add_argument(
        "--max-pad-bits",
        type=int,
        default=16,
        help="Maximum number of padding bits to brute force (default: 16)",
    )
    args = ap.parse_args()

    vec_dir = args.dir
    name = args.name
    station_id = args.station_id

    in_txt = vec_dir / f"{name}.format_101.txt"
    exp_hpk = vec_dir / f"{name}.expected.hpk.txt"

    exp_oct = load_expected_octets(exp_hpk)

    msg = encode_format101_from_txt(
        format101_txt=in_txt,
        pilote_csv=args.pilote,
        station_id=station_id,
        finalize=False,
    )

    base_bits = msg.unfinalized_bits
    base_out = bytearray(msg.unfinalized_octets)
    # Ensure buffer is large enough for writes.
    # We may append up to max_pad_bits and then byte-align.
    worst_bits = base_bits + args.max_pad_bits + 7
    worst_bytes = (worst_bits + 7) // 8
    if len(base_out) < worst_bytes:
        base_out.extend(b"\x00" * (worst_bytes - len(base_out)))

    best: Solution | None = None

    for pad_len in range(0, args.max_pad_bits + 1):
        # iterate over all bit patterns of length pad_len
        for pad_val in range(0, 1 << pad_len):
            out = bytearray(base_out)
            b_ofs = base_bits

            if pad_len:
                b_ofs = write_bits(out, b_ofs, pad_len, pad_val)

            # always byte-align with zeros
            pad8 = (-b_ofs) % 8
            if pad8:
                b_ofs = write_bits(out, b_ofs, pad8, 0)

            octets = bytes(out[: (b_ofs + 7) // 8])
            if octets != exp_oct:
                continue

            pad_bits_str = (
                format(pad_val, f"0{pad_len}b") if pad_len else ""
            )  # MSB-first
            sol = Solution(
                pad_bits=pad_bits_str,
                final_bits=b_ofs,
                final_octets_len=len(octets),
            )

            if best is None:
                best = sol
            else:
                # Prefer fewer pad bits; if tie, prefer lexicographically smaller bitstring
                if (len(sol.pad_bits) < len(best.pad_bits)) or (
                    len(sol.pad_bits) == len(best.pad_bits)
                    and sol.pad_bits < best.pad_bits
                ):
                    best = sol

        if best is not None:
            # minimal pad_len found
            break

    print(f"name={name}")
    print(f"station_id={station_id}")
    print(f"expected_octets_len={len(exp_oct)}")
    print(f"base_unfinalized_bits={base_bits}")
    print(f"base_unfinalized_octets_len={len(msg.unfinalized_octets)}")

    if best is None:
        print("no solution found within max-pad-bits")
        return 2

    print("== solution ==")
    print(f"pad_bits_len={len(best.pad_bits)}")
    print(f"pad_bits={best.pad_bits!r}")
    print(f"final_bits={best.final_bits}")
    print(f"final_octets_len={best.final_octets_len}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

import argparse
import random

from format101.codec6 import (
    compact_6bit_text_to_octets,
    encode_octets_to_6bit_words,
)


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Self-test: compact(decompact(octets)) == octets"
    )
    ap.add_argument("--count", type=int, default=200, help="Number of random cases")
    ap.add_argument("--max-len", type=int, default=64, help="Max octet length")
    ap.add_argument("--seed", type=int, default=1234, help="RNG seed")
    args = ap.parse_args()

    rng = random.Random(args.seed)

    for i in range(args.count):
        n = rng.randint(0, args.max_len)
        octets = bytes(rng.getrandbits(8) for _ in range(n))
        words = encode_octets_to_6bit_words(octets)
        back = compact_6bit_text_to_octets(words)
        if back != octets:
            print(f"[FAIL] case={i} len={n}")
            print("expected:", octets.hex())
            print("got     :", back.hex())
            return 1

    print(f"[OK] {args.count} cases")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

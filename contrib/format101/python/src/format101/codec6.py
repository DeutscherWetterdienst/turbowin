def compact_6bit_text_to_octets(text: bytes) -> bytes:
    """
    Reassemble 6-bit words (stored in the lower 6 bits of each byte) as 8-bit octets.

    This is based on a reference implementation found in the ecosystem around MAWSbin.
    """
    octets: list[int] = []
    buf = 0
    rss = [0, 4, 0, 2, 0, 0]  # right-shift amounts
    lss = [2, 0, 4, 0, 6, 0]  # left-shift amounts
    bcs = [6, 2, 4, 4, 2, 6]  # bits put in byte amounts
    i = 0  # step counter
    oc = 0  # input index
    bc = 0  # bits accumulated from 6-bit words
    while oc < len(text):
        bc += bcs[i]
        buf |= (((text[oc] & 63) >> rss[i]) << lss[i]) & 255
        if i in (1, 3, 5):
            octets.append(buf)
            buf = 0
        i += 1
        if i == 6:
            i = 0
        if bc == 6:
            bc = 0
            oc += 1
    return bytes(octets)


def expand_6bit_text_to_octets(text: bytes) -> bytes:
    """
    Decode TurboWin+ half-compressed payload bytes into the full 8-bit octet stream.

    TurboWin+'s encoder produces a payload where each byte is in the range 0x40..0x7F
    and encodes a 6-bit value in the lower bits, i.e.:

        six = (byte - 0x40) & 0x3F

    This matches the observed TurboWin+ payload (lots of 0x7F bytes for missing data).
    """
    return compact_6bit_text_to_octets(bytes(((b - 0x40) & 0x3F) for b in text))


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    This is the inverse of expand_6bit_text_to_octets(), but the compaction algorithm
    is not bijective due to bit shifts and OR-composition. Therefore we deterministically
    synthesize the 6-bit word stream by searching for the lexicographically smallest
    words that reproduce each target output byte when decoded.

    The output is a byte string where each byte is in the range 0x40..0x7F and encodes
    a 6-bit value in its lower bits.
    """
    if not octets:
        return b""

    rss = [0, 4, 0, 2, 0, 0]  # right-shift amounts
    lss = [2, 0, 4, 0, 6, 0]  # left-shift amounts

    def contrib(word: int, i: int) -> int:
        return (((word & 0x3F) >> rss[i]) << lss[i]) & 0xFF

    out_words: list[int] = []

    # The decoder emits one octet after steps (0,1), (2,3), and (4,5).
    # We therefore solve 2x 6-bit words per output octet, and produce up to 6 words
    # for each group of 3 output octets.
    idx = 0
    src = list(octets)
    while idx < len(src):
        targets = src[idx : idx + 3]
        idx += 3
        while len(targets) < 3:
            targets.append(None)

        cycle_words: list[int] = []

        for j, (i0, i1) in enumerate(((0, 1), (2, 3), (4, 5))):
            tgt = targets[j]
            if tgt is None:
                break

            best: tuple[int, int] | None = None
            for w0 in range(64):
                c0 = contrib(w0, i0)
                # Since output is built by OR-ing contributions, c0 must not set bits
                # outside the target byte.
                if (c0 | tgt) != tgt:
                    continue
                for w1 in range(64):
                    c = c0 | contrib(w1, i1)
                    if c == tgt:
                        best = (w0, w1)
                        break
                if best is not None:
                    break

            if best is None:
                raise ValueError(
                    f"Could not encode target byte 0x{tgt:02x} at cycle part {j}"
                )

            cycle_words.extend(best)

        out_words.extend(cycle_words)

    return bytes(((w & 0x3F) + 0x40) & 0xFF for w in out_words)

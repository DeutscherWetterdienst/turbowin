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
    """
    return compact_6bit_text_to_octets(bytes(((b - 0x40) & 0x3F) for b in text))


def decode_turbowin_text_to_octets(text: bytes) -> bytes:
    """
    Decode TurboWin+ half-compressed payload bytes into the full 8-bit octet stream.

    Alias for expand_6bit_text_to_octets(), kept for clearer naming.
    """
    return expand_6bit_text_to_octets(text)


def _build_compact_mapping():
    """
    Build a bit mapping for compact_6bit_text_to_octets over a 24-bit window:
    4 words (24 bits) -> 3 octets (24 bits)

    Returns:
      - inbit_to_outbit: list of length 24 mapping each input bit to an output bit index
      - out_len_bits: number of output bits (should be 24)
    """
    rss = [0, 4, 0, 2, 0, 0]
    lss = [2, 0, 4, 0, 6, 0]
    bcs = [6, 2, 4, 4, 2, 6]

    def compact_bits(in_bits: list[int]) -> list[int]:
        if len(in_bits) % 6 != 0:
            raise ValueError("in_bits length must be multiple of 6")
        nwords = len(in_bits) // 6
        words = []
        for wi in range(nwords):
            v = 0
            for bi in range(6):
                v = (v << 1) | (in_bits[wi * 6 + bi] & 1)
            words.append(v)

        out_octets: list[int] = []
        buf = 0
        i = 0
        oc = 0
        bc = 0
        while oc < len(words):
            bc += bcs[i]
            buf |= (((words[oc] & 63) >> rss[i]) << lss[i]) & 255
            if i in (1, 3, 5):
                out_octets.append(buf)
                buf = 0
            i += 1
            if i == 6:
                i = 0
            if bc == 6:
                bc = 0
                oc += 1

        out_bits: list[int] = []
        for b in out_octets:
            for k in range(8):
                out_bits.append((b >> (7 - k)) & 1)
        return out_bits

    in_len = 24
    mapping = [-1] * in_len

    for in_pos in range(in_len):
        bits = [0] * in_len
        bits[in_pos] = 1
        out_bits = compact_bits(bits)
        ones = [i for i, v in enumerate(out_bits) if v == 1]
        if len(ones) != 1:
            raise RuntimeError(f"Unexpected mapping for in_pos={in_pos}: ones={ones}")
        mapping[in_pos] = ones[0]

    out_len = max(mapping) + 1
    return mapping, out_len


_COMPACT_INBIT_TO_OUTBIT, _COMPACT_OUTLEN_BITS = _build_compact_mapping()


def _invert_block_3octets_to_4words(block: bytes) -> bytes:
    if len(block) != 3:
        raise ValueError("block must be exactly 3 octets")

    out_bits: list[int] = []
    for b in block:
        for k in range(8):
            out_bits.append((b >> (7 - k)) & 1)

    ib = [0] * 24
    for in_pos, out_pos in enumerate(_COMPACT_INBIT_TO_OUTBIT):
        ib[in_pos] = out_bits[out_pos]

    words_out = bytearray()
    for wi in range(4):
        v = 0
        for bi in range(6):
            v = (v << 1) | (ib[wi * 6 + bi] & 1)
        words_out.append(v & 0x3F)
    return bytes(words_out)


def encode_octets_to_6bit_words(octets: bytes) -> bytes:
    """
    Invert compact_6bit_text_to_octets for TurboWin/MAWSbin-style compaction.

    This returns a 6-bit word sequence that compacts to the given octets prefix.
    It does not attempt to "canonicalize" the result, because multiple 6-bit sequences
    may compact to the same octet stream.
    """
    if not octets:
        return b""

    if _COMPACT_OUTLEN_BITS != 24:
        raise RuntimeError("Unexpected compact mapping output length")

    words = bytearray()
    for i in range(0, len(octets), 3):
        block = octets[i : i + 3]
        if len(block) < 3:
            # pad missing bytes with 0xFF (all-ones) for tail robustness
            block = block + b"\xff" * (3 - len(block))
        words.extend(_invert_block_3octets_to_4words(block))

    # Ensure the produced words compact to a stream that starts with octets
    out = compact_6bit_text_to_octets(words)
    if out[: len(octets)] != octets:
        raise RuntimeError(
            "Internal error: decompaction candidate does not match input octets prefix"
        )

    # Trim trailing words while still producing a stream with the correct prefix length
    # (compact can emit extra octets due to padding of the last 3-octet block)
    while len(words) > 0:
        cand = words[:-1]
        if not cand:
            break
        out_cand = compact_6bit_text_to_octets(cand)
        if out_cand[: len(octets)] == octets:
            words = bytearray(cand)
            continue
        break

    return bytes(words)


def encode_octets_to_turbowin_text(octets: bytes) -> bytes:
    words = encode_octets_to_6bit_words(octets)
    return bytes(((w & 0x3F) + 0x40) for w in words)


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Backwards-compatible alias used by encoder.py.
    """
    return encode_octets_to_turbowin_text(octets)

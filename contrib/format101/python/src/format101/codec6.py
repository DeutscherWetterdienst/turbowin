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


def _encode_octets_to_6bit_text_base(octets: bytes) -> bytes:
    """
    Base encoder: expand payload octets into 6-bit words MSB-first and map to 0x40..0x7F.
    This does not guarantee the same canonical 6-bit text tail as the reference encoder,
    because the 6-bit text compaction is not bijective.
    """
    if not octets:
        return b""

    nbits = len(octets) * 8
    nwords = (nbits + 5) // 6  # ceil
    out = bytearray()

    bitpos = 0
    for _ in range(nwords):
        val = 0
        for _i in range(6):
            if bitpos >= nbits:
                val <<= 1
                continue
            byte_idx = bitpos // 8
            bit_in_byte = bitpos % 8
            bit = (octets[byte_idx] >> (7 - bit_in_byte)) & 1
            val = (val << 1) | bit
            bitpos += 1
        out.append((val & 0x3F) + 0x40)

    return bytes(out)


def _minimize_text_length(text: bytes, *, target_octets: bytes) -> bytes:
    """
    Remove as many trailing 6-bit words as possible while preserving decoded octets.
    """
    cur = text
    while len(cur) > 0:
        shorter = cur[:-1]
        if decode_turbowin_text_to_octets(shorter) == target_octets:
            cur = shorter
            continue
        break
    return cur


def _find_lexicographically_smallest_tail(
    *,
    prefix: bytes,
    k: int,
    target_octets: bytes,
) -> bytes | None:
    """
    Find the lexicographically smallest tail of length k (each byte 0x40..0x7F) such that
    decode(prefix + tail) == target_octets.

    This is brute force but limited to k in {1,2} for performance.
    """
    if k == 1:
        for w0 in range(64):
            cand = prefix + bytes([0x40 + w0])
            if decode_turbowin_text_to_octets(cand) == target_octets:
                return cand
        return None

    if k == 2:
        for w0 in range(64):
            b0 = 0x40 + w0
            for w1 in range(64):
                cand = prefix + bytes([b0, 0x40 + w1])
                if decode_turbowin_text_to_octets(cand) == target_octets:
                    return cand
        return None

    raise ValueError("Unsupported tail length")


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    The mapping from 6-bit text to octets (compaction) is not bijective. Therefore,
    multiple text representations can decode to the same octet stream.

    This function enforces two invariants:
    1) The returned text MUST round-trip:
         decode_turbowin_text_to_octets(text) == octets
    2) The returned text is canonicalized (deterministic), using:
       - minimal possible length while preserving decoded octets
       - lexicographically smallest tail replacement (k=1 preferred, then k=2)
    """
    target = octets
    base = _encode_octets_to_6bit_text_base(target)
    if not base:
        return base

    # Ensure we work with a representation that round-trips; otherwise we cannot canonicalize safely.
    if decode_turbowin_text_to_octets(base) != target:
        raise ValueError(
            "Internal error: base 8->6 encoding does not round-trip to target octets"
        )

    # 1) Minimize the text length while preserving decoded octets
    base = _minimize_text_length(base, target_octets=target)

    # 2) Canonicalize tail by searching for the lexicographically smallest tail replacement
    # Prefer k=1 tail replacement when available (matches observed reference outputs).
    cand1 = None
    cand2 = None

    if len(base) >= 1:
        cand1 = _find_lexicographically_smallest_tail(
            prefix=base[:-1],
            k=1,
            target_octets=target,
        )

    if len(base) >= 2:
        cand2 = _find_lexicographically_smallest_tail(
            prefix=base[:-2],
            k=2,
            target_octets=target,
        )

    if cand1 is not None:
        result = cand1
    elif cand2 is not None:
        result = cand2
    else:
        result = base

    # Hard gate: must round-trip exactly
    if decode_turbowin_text_to_octets(result) != target:
        raise ValueError(
            "Internal error: canonicalized 6-bit text does not round-trip to target octets"
        )

    return result

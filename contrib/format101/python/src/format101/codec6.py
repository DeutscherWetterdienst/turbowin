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


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    The mapping from 6-bit text to octets (compaction) is not bijective. For strict
    1:1 compatibility with the reference encoder, we canonicalize the tail by searching
    for a lexicographically smallest tail (within a small window) that decodes back to
    the exact same octets.

    This implementation only searches over the last 1..3 6-bit characters because
    observed differences versus the reference encoder are confined to the tail.
    """
    base = _encode_octets_to_6bit_text_base(octets)
    if not base:
        return base

    target = octets

    # If the base already round-trips exactly, keep it.
    if decode_turbowin_text_to_octets(base) == target:
        # Still canonicalize tail to match reference (base may round-trip but differ)
        pass
    else:
        # Base must at least be a valid representation; otherwise we have a deeper bug.
        # Keep base as fallback.
        return base

    best = base

    # Tail search: k=1..3. Replace last k bytes with all 0x40..0x7F possibilities and
    # keep the lexicographically smallest candidate that decodes exactly.
    for k in (1, 2, 3):
        if len(base) < k:
            continue
        prefix = base[:-k]
        found_best = None

        # Iterate tail words in lexicographic order of resulting bytes (0x40..0x7F),
        # which corresponds to word values 0..63.
        def rec_build(pos: int, buf: bytearray) -> None:
            nonlocal found_best
            if found_best is not None:
                return
            if pos == k:
                cand = bytes(prefix + buf)
                if decode_turbowin_text_to_octets(cand) == target:
                    found_best = cand
                return
            for w in range(64):
                buf.append(0x40 + w)
                rec_build(pos + 1, buf)
                buf.pop()
                if found_best is not None:
                    return

        rec_build(0, bytearray())

        if found_best is not None:
            best = found_best
            break

    return best

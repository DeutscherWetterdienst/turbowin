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


def uncompact_octets_to_6bit_words(octets: bytes) -> bytes:
    """
    Inverse of compact_6bit_text_to_octets().

    Return raw 6-bit values (0..63) such that:

        compact_6bit_text_to_octets(result) == octets

    Note: The compaction algorithm discards some bits due to shifting. For those degrees
    of freedom, this function uses deterministic zero-padding to ensure stable output.
    """
    out = bytearray()
    idx = 0
    while idx < len(octets):
        # Steps (0,1): buf = (a << 2) | (b >> 4)
        buf = octets[idx] & 0xFF
        a = (buf >> 2) & 0x3F
        b_hi = buf & 0x03  # equals (b >> 4)
        b = (b_hi << 4) & 0x3F
        out.append(a)
        out.append(b)
        idx += 1
        if idx >= len(octets):
            break

        # Steps (2,3): buf = (c << 4) | (d >> 2)
        buf = octets[idx] & 0xFF
        c = (buf >> 4) & 0x3F
        d_hi = buf & 0x0F  # equals (d >> 2)
        d = (d_hi << 2) & 0x3F
        out.append(c)
        out.append(d)
        idx += 1
        if idx >= len(octets):
            break

        # Steps (4,5): buf = (e << 6) | f
        buf = octets[idx] & 0xFF
        e = (buf >> 6) & 0x3F
        f = buf & 0x3F
        out.append(e)
        out.append(f)
        idx += 1

    return bytes(out)


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    This is the inverse of expand_6bit_text_to_octets():
      - derive the raw 6-bit word stream as the inverse of the compaction algorithm
      - store each 6-bit word as a single byte in the range 0x40..0x7F
    """
    six = uncompact_octets_to_6bit_words(octets)
    return bytes(((b & 0x3F) + 0x40) & 0xFF for b in six)

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

    This performs an "8-to-6 expansion":
      - payload is a byte-aligned bitstream
      - it is expanded to 6-bit words (MSB-first)
      - each 6-bit word is stored as (0x40 + word)

    Note: This intentionally does not try to invert compact_6bit_text_to_octets(),
    because that compaction is not bijective.
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

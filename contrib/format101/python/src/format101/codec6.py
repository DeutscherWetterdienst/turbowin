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


def _bit_at(octets: bytes, bitpos: int) -> int:
    byte_idx = bitpos // 8
    bit_in_byte = bitpos % 8
    return (octets[byte_idx] >> (7 - bit_in_byte)) & 1


def encode_octets_to_6bit_words(octets: bytes) -> bytes:
    """
    Encode octets into 6-bit words (0..63) so that:

        compact_6bit_text_to_octets(words) == octets

    This is the inverse operation required to reproduce TurboWin/MAWSbin payload text.

    The compaction packs 6-bit words into bytes in a fixed MSB-first bit order, so the
    inverse is simply splitting the octet bitstream into 6-bit groups MSB-first, while
    padding the final group with 1-bits (legacy all-ones missing convention) if needed.
    """
    if not octets:
        return b""

    nbits = len(octets) * 8
    nwords = (nbits + 5) // 6  # ceil
    out = bytearray()

    bitpos = 0
    for _ in range(nwords):
        v = 0
        for _i in range(6):
            if bitpos < nbits:
                b = _bit_at(octets, bitpos)
            else:
                # pad with 1-bits to match legacy all-ones behavior at the tail
                b = 1
            v = (v << 1) | b
            bitpos += 1
        out.append(v & 0x3F)

    return bytes(out)


def encode_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    This uses the inverse of compact_6bit_text_to_octets() (via encode_octets_to_6bit_words).
    """
    words = encode_octets_to_6bit_words(octets)
    return bytes(((w & 0x3F) + 0x40) for w in words)


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Backwards-compatible alias used by encoder.py.
    """
    return encode_octets_to_turbowin_text(octets)

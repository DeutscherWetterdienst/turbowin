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


def encode_octets_to_6bit_words(octets: bytes) -> bytes:
    """
    Inverse of compact_6bit_text_to_octets(): reconstruct the original 6-bit words
    from octets, so that:

        compact_6bit_text_to_octets(encode_octets_to_6bit_words(octets)) == octets

    This implements the inverse mapping of the 6-step packing pattern used by the
    ecosystem reference decoder. It yields a deterministic 6-bit word stream.
    """
    if not octets:
        return b""

    out = bytearray()

    # For every 3 octets, we can reconstruct 4 6-bit words.
    # Derivation follows the compacting steps:
    #   o0 uses bits from w0 and w1
    #   o1 uses bits from w1 and w2
    #   o2 uses bits from w2 and w3
    i = 0
    n = len(octets)
    while i + 3 <= n:
        o0 = octets[i] & 0xFF
        o1 = octets[i + 1] & 0xFF
        o2 = octets[i + 2] & 0xFF

        w0 = (o0 >> 2) & 0x3F
        w1 = ((o0 & 0x03) << 4) | ((o1 >> 4) & 0x0F)
        w2 = ((o1 & 0x0F) << 2) | ((o2 >> 6) & 0x03)
        w3 = o2 & 0x3F

        out.extend((w0, w1, w2, w3))
        i += 3

    # Handle remaining octets (1 or 2). These correspond to 2 or 3 6-bit words.
    rem = n - i
    if rem == 1:
        o0 = octets[i] & 0xFF
        w0 = (o0 >> 2) & 0x3F
        w1 = (o0 & 0x03) << 4  # remaining 4 bits unknown -> 0
        out.extend((w0, w1))
    elif rem == 2:
        o0 = octets[i] & 0xFF
        o1 = octets[i + 1] & 0xFF
        w0 = (o0 >> 2) & 0x3F
        w1 = ((o0 & 0x03) << 4) | ((o1 >> 4) & 0x0F)
        w2 = (o1 & 0x0F) << 2  # remaining 2 bits unknown -> 0
        out.extend((w0, w1, w2))

    return bytes(out)


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    This uses the inverse of the ecosystem compaction algorithm (compact_6bit_text_to_octets),
    yielding a deterministic 6-bit word stream, then maps each 6-bit word to a text byte
    by adding 0x40.
    """
    words = encode_octets_to_6bit_words(octets)
    return bytes(((w & 0x3F) + 0x40) for w in words)

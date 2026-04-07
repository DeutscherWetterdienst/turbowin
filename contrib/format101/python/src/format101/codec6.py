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


def pack_octets_to_6bit_text(octets: bytes) -> bytes:
    """
    Pack an 8-bit octet stream into 6-bit words.

    This is the inverse of compact_6bit_text_to_octets() for whole-byte payloads.
    It returns raw 6-bit values in the range 0..63 (not yet ASCII shifted).

    The reference decoder compacts by consuming 6-bit input words and producing 8-bit
    output octets in a fixed 6-step cycle. Inverting this produces 4x 6-bit output
    words for every 3 input octets (i.e. 24 bits -> 4x6 bits).
    """
    if not octets:
        return b""

    # Pad with zeros to a multiple of 3 bytes so we can pack in 24-bit groups.
    # The bitstream is already padded to whole bytes by the encoder.
    pad = (-len(octets)) % 3
    if pad:
        octets = octets + (b"\x00" * pad)

    out = bytearray()
    for i in range(0, len(octets), 3):
        v = (octets[i] << 16) | (octets[i + 1] << 8) | octets[i + 2]
        out.append((v >> 18) & 0x3F)
        out.append((v >> 12) & 0x3F)
        out.append((v >> 6) & 0x3F)
        out.append(v & 0x3F)
    return bytes(out)


def encode_payload_octets_to_turbowin_text(octets: bytes) -> bytes:
    """
    Encode payload octets into TurboWin+ half-compressed text bytes (0x40..0x7F).

    This is the inverse of expand_6bit_text_to_octets():
      - pack octets to 6-bit words
      - store each 6-bit word as a single byte in the range 0x40..0x7F
    """
    six = pack_octets_to_6bit_text(octets)
    return bytes((b + 0x40) & 0xFF for b in six)

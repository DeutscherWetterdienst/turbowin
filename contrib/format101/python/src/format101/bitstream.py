def read_bits(
    octets: bytes,
    bits_offset: int,
    nbits: int,
    *,
    treat_all_ones_as_missing: bool = True,
) -> tuple[int, int | None]:
    """
    Read nbits from octets starting at bits_offset.

    Missing value convention (legacy): all bits set to 1 => None.
    This convention must be disabled for certain 1-bit control fields (e.g. group markers),
    where the value 1 is a valid value and must not be treated as missing.
    """
    octet_start = bits_offset // 8
    octet_idx = octet_start
    bit_start = bits_offset % 8
    bit_rest = (8 - ((bit_start + nbits) % 8)) % 8
    val = 0
    procbits = -bit_start
    while procbits < nbits:
        if octet_idx > octet_start:
            val <<= 8
        val |= octets[octet_idx] & 0xFF
        octet_idx += 1
        procbits += 8
    val = (val >> bit_rest) & ((1 << nbits) - 1)
    if treat_all_ones_as_missing and (val ^ ((1 << nbits) - 1)) == 0:
        return bits_offset + nbits, None
    return bits_offset + nbits, val


def write_bits(
    out: bytearray,
    bits_offset: int,
    nbits: int,
    value: int,
) -> int:
    """
    Write nbits of value into out starting at bits_offset.

    Bit layout matches read_bits(): values are written MSB-first into the bitstream.

    The output buffer (out) is grown as needed.
    """
    if nbits <= 0:
        return bits_offset

    mask = (1 << nbits) - 1
    value &= mask

    end_bit = bits_offset + nbits
    needed_bytes = (end_bit + 7) // 8
    if len(out) < needed_bytes:
        out.extend(b"\x00" * (needed_bytes - len(out)))

    for i in range(nbits):
        bit_val = (value >> (nbits - 1 - i)) & 1
        abs_bit = bits_offset + i
        byte_idx = abs_bit // 8
        bit_in_byte = abs_bit % 8
        shift = 7 - bit_in_byte
        if bit_val:
            out[byte_idx] |= 1 << shift
        else:
            out[byte_idx] &= ~(1 << shift)

    return bits_offset + nbits

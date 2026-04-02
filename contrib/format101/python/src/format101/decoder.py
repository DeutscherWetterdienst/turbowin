from dataclasses import dataclass
from pathlib import Path

from format101.bitstream import read_bits
from format101.codec6 import expand_6bit_text_to_octets
from format101.spec import PilotEntry, load_pilote_csv


@dataclass(frozen=True)
class DecodedField:
    key: str
    value: float | int | None


@dataclass(frozen=True)
class DecodedMessage:
    station_id_raw: str
    station_id: str
    template: str
    fields: tuple[DecodedField, ...]

    def to_format101_txt(self) -> str:
        """
        Render a canonical TurboWin-style format_101.txt:
        - first line "0"
        - then per field: "0" (missing) or "1 <value>"
        """
        lines: list[str] = ["0"]
        for f in self.fields:
            if f.value is None:
                lines.append("0")
            else:
                # Use a stable, compact formatting (TurboWin accepts floats)
                if isinstance(f.value, int):
                    lines.append(f"1 {f.value}.0")
                else:
                    lines.append(f"1 {f.value}")
        return "\n".join(lines) + "\n"


def _decode_fields(octets: bytes, pilote: list[PilotEntry]) -> list[DecodedField]:
    b_ofs = 0
    out: list[DecodedField] = []
    n_total_bits = len(octets) * 8

    i = 0
    while i < len(pilote):
        desc = pilote[i]
        key = f"{desc.bufr}{('_' + desc.ref) if desc.ref else ''}"

        # Some TurboWin+ messages omit optional groups. In that case, the payload is shorter
        # than the full pilote definition. If we run out of bits, treat remaining fields as missing.
        if b_ofs + desc.nbits > n_total_bits:
            out.append(DecodedField(key=key, value=None))
            i += 1
            for rest in pilote[i:]:
                rest_key = f"{rest.bufr}{('_' + rest.ref) if rest.ref else ''}"
                out.append(DecodedField(key=rest_key, value=None))
            break

        b_ofs, raw = read_bits(octets, b_ofs, desc.nbits)
        if raw is None:
            out.append(DecodedField(key=key, value=None))
            i += 1
            continue

        val = raw * desc.factor + desc.offset
        if desc.factor >= 1 and float(val).is_integer():
            value: float | int = int(val)
        else:
            value = float(val)

        out.append(DecodedField(key=key, value=value))

        # NOTE: In TurboWin legacy vectors, the group markers are always present and the
        # group fields are still part of the bitstream even when absent (encoded as missing).
        # Therefore we do not skip group fields when the marker is 0.
        i += 1

    return out


def decode_hpk_line(
    hpk_line: str,
    *,
    pilote_csv: str | Path,
    template: str = "S-AWS-101",
) -> DecodedMessage:
    """
    Decode a single TurboWin+ HPK_format_101.txt line.

    The line consists of:
    - 7-character station id prefix (left padded with spaces)
    - followed by a half-compressed payload
    """
    pilote = load_pilote_csv(pilote_csv)

    raw = hpk_line.rstrip("\r\n")
    prefix = raw[:7]
    station_id = prefix.strip()

    # The payload contains bytes like 0x7F (DEL), so we must treat it as raw bytes.
    payload_text = raw[7:].encode("latin1")

    # TurboWin payload bytes are in the range 0x40..0x7F; convert to 6-bit values and compact.
    octets = expand_6bit_text_to_octets(payload_text)

    # TurboWin's legacy pilot CSV includes an initial '000000' (operating mode) entry.
    # That value is not part of the encoded payload, so we skip it when decoding.
    if pilote and pilote[0].bufr == "000000" and pilote[0].ref == "":
        pilote = pilote[1:]

    fields = _decode_fields(octets, pilote)
    return DecodedMessage(
        station_id_raw=prefix,
        station_id=station_id,
        template=template,
        fields=tuple(fields),
    )

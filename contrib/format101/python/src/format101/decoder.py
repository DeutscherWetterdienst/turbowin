from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

from format101.bitstream import read_bits
from format101.codec6 import compact_6bit_text_to_octets
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


def _decode_fields(octets: bytes, pilote: Iterable[PilotEntry]) -> list[DecodedField]:
    b_ofs = 0
    out: list[DecodedField] = []
    for desc in pilote:
        key = f"{desc.bufr}{('_' + desc.ref) if desc.ref else ''}"
        b_ofs, raw = read_bits(octets, b_ofs, desc.nbits)
        if raw is None:
            out.append(DecodedField(key=key, value=None))
            continue

        val = raw * desc.factor + desc.offset
        if desc.factor >= 1 and float(val).is_integer():
            out.append(DecodedField(key=key, value=int(val)))
        else:
            out.append(DecodedField(key=key, value=float(val)))
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
    - followed by a half-compressed payload (6-bit words stored in bytes)
    """
    pilote = load_pilote_csv(pilote_csv)

    raw = hpk_line.rstrip("\r\n")
    prefix = raw[:7]
    station_id = prefix.strip()
    payload_text = raw[7:].encode("latin1")
    octets = compact_6bit_text_to_octets(payload_text)

    fields = _decode_fields(octets, pilote)
    return DecodedMessage(
        station_id_raw=prefix,
        station_id=station_id,
        template=template,
        fields=tuple(fields),
    )

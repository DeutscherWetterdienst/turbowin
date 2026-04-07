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


VISUAL_COUNT = 10
WAVE_COUNT = 8
ICE_COUNT = 8


def _field_key(desc: PilotEntry) -> str:
    return f"{desc.bufr}{('_' + desc.ref) if desc.ref else ''}"


def _decode_entry(
    octets: bytes,
    bits_offset: int,
    desc: PilotEntry,
    *,
    key_override: str | None = None,
) -> tuple[int, DecodedField]:
    key = key_override if key_override is not None else _field_key(desc)
    bits_offset, raw = read_bits(octets, bits_offset, desc.nbits)
    if raw is None:
        return bits_offset, DecodedField(key=key, value=None)

    val = raw * desc.factor + desc.offset
    if desc.factor >= 1 and float(val).is_integer():
        value: float | int = int(val)
    else:
        value = float(val)
    return bits_offset, DecodedField(key=key, value=value)


def _decode_block(
    octets: bytes, bits_offset: int, entries: list[PilotEntry]
) -> tuple[int, list[DecodedField]]:
    out: list[DecodedField] = []
    for e in entries:
        bits_offset, field = _decode_entry(octets, bits_offset, e)
        out.append(field)
    return bits_offset, out


def _missing_block(entries: list[PilotEntry]) -> list[DecodedField]:
    return [DecodedField(key=_field_key(e), value=None) for e in entries]


def _split_pilote_into_sections(
    pilote: list[PilotEntry],
) -> tuple[
    list[PilotEntry],
    PilotEntry,
    list[PilotEntry],
    PilotEntry,
    list[PilotEntry],
    PilotEntry,
    list[PilotEntry],
]:
    """
    Split the TurboWin legacy S-AWS-101 pilote file into logical sections.

    Layout (after skipping 000000 operating mode):
    - main block ends at 022042 (SST)
    - 410000 replication marker (visual)
    - 10 visual fields (020001..020013)
    - 408000 replication marker (wave)
    - 8 wave fields (022012..022023)
    - 408000 replication marker (ice)
    - 8 ice fields (020031..020038)
    """
    idx_022042 = next(i for i, e in enumerate(pilote) if e.bufr == "022042")
    if idx_022042 + 1 >= len(pilote):
        raise ValueError("Invalid pilote file: missing 410000 visual marker")

    main = pilote[: idx_022042 + 1]

    idx_vis_marker = idx_022042 + 1
    vis_marker = pilote[idx_vis_marker]
    if vis_marker.bufr != "410000":
        raise ValueError(
            f"Invalid pilote file: expected 410000 visual marker after 022042, got {vis_marker.bufr}"
        )

    idx_vis_fields_start = idx_vis_marker + 1
    idx_vis_fields_end = idx_vis_fields_start + VISUAL_COUNT
    if idx_vis_fields_end > len(pilote):
        raise ValueError("Invalid pilote file: insufficient visual fields after 410000")
    vis_fields = pilote[idx_vis_fields_start:idx_vis_fields_end]

    idx_wave_marker = idx_vis_fields_end
    if idx_wave_marker >= len(pilote):
        raise ValueError("Invalid pilote file: missing first 408000 wave marker")
    wave_marker = pilote[idx_wave_marker]
    if wave_marker.bufr != "408000":
        raise ValueError(
            f"Invalid pilote file: expected 408000 wave marker, got {wave_marker.bufr}"
        )

    idx_wave_fields_start = idx_wave_marker + 1
    idx_wave_fields_end = idx_wave_fields_start + WAVE_COUNT
    if idx_wave_fields_end > len(pilote):
        raise ValueError(
            "Invalid pilote file: insufficient wave fields after first 408000"
        )
    wave_fields = pilote[idx_wave_fields_start:idx_wave_fields_end]

    idx_ice_marker = idx_wave_fields_end
    if idx_ice_marker >= len(pilote):
        raise ValueError("Invalid pilote file: missing second 408000 ice marker")
    ice_marker = pilote[idx_ice_marker]
    if ice_marker.bufr != "408000":
        raise ValueError(
            f"Invalid pilote file: expected 408000 ice marker, got {ice_marker.bufr}"
        )

    idx_ice_fields_start = idx_ice_marker + 1
    idx_ice_fields_end = idx_ice_fields_start + ICE_COUNT
    if idx_ice_fields_end > len(pilote):
        raise ValueError(
            "Invalid pilote file: insufficient ice fields after second 408000"
        )
    ice_fields = pilote[idx_ice_fields_start:idx_ice_fields_end]

    return (
        main,
        vis_marker,
        vis_fields,
        wave_marker,
        wave_fields,
        ice_marker,
        ice_fields,
    )


def _decode_fields(octets: bytes, pilote: list[PilotEntry]) -> list[DecodedField]:
    """
    Decode octets according to pilote definitions.

    TurboWin's legacy S-AWS-101 pilote uses BUFR-style short delayed replication factors:
    - 410000: visual group replication indicator (0/1)
    - 408000: wave group replication indicator (0/1)
    - 408000: ice group replication indicator (0/1)

    These are control bits for a variable-length message. If a marker is 0,
    the subsequent block is not present in the bitstream and must be skipped.
    """
    (
        main,
        vis_marker,
        vis_fields,
        wave_marker,
        wave_fields,
        ice_marker,
        ice_fields,
    ) = _split_pilote_into_sections(pilote)

    out: list[DecodedField] = []
    bits_offset = 0
    n_total_bits = len(octets) * 8

    def have_bits(nbits: int) -> bool:
        return bits_offset + nbits <= n_total_bits

    def decode_block_or_missing(entries: list[PilotEntry]) -> list[DecodedField]:
        nonlocal bits_offset
        res: list[DecodedField] = []
        for e in entries:
            if have_bits(e.nbits):
                bits_offset, f = _decode_entry(octets, bits_offset, e)
                res.append(f)
            else:
                res.append(DecodedField(key=_field_key(e), value=None))
        return res

    def to_int01(v: float | int | None) -> int | None:
        if v is None:
            return None
        return int(v)

    # main block
    out.extend(decode_block_or_missing(main))

    # visual marker + fields
    vis_val: int | None = None
    if have_bits(vis_marker.nbits):
        bits_offset, vis_m = _decode_entry(
            octets, bits_offset, vis_marker, key_override="410000_visual"
        )
        out.append(vis_m)
        vis_val = to_int01(vis_m.value)
    else:
        out.append(DecodedField(key="410000_visual", value=None))
    if vis_val == 1:
        out.extend(decode_block_or_missing(vis_fields))
    else:
        out.extend(_missing_block(vis_fields))

    # wave marker + fields
    wave_val: int | None = None
    if have_bits(wave_marker.nbits):
        bits_offset, wave_m = _decode_entry(
            octets, bits_offset, wave_marker, key_override="408000_wave"
        )
        out.append(wave_m)
        wave_val = to_int01(wave_m.value)
    else:
        out.append(DecodedField(key="408000_wave", value=None))
    if wave_val == 1:
        out.extend(decode_block_or_missing(wave_fields))
    else:
        out.extend(_missing_block(wave_fields))

    # ice marker + fields
    ice_val: int | None = None
    if have_bits(ice_marker.nbits):
        bits_offset, ice_m = _decode_entry(
            octets, bits_offset, ice_marker, key_override="408000_ice"
        )
        out.append(ice_m)
        ice_val = to_int01(ice_m.value)
    else:
        out.append(DecodedField(key="408000_ice", value=None))
    if ice_val == 1:
        out.extend(decode_block_or_missing(ice_fields))
    else:
        out.extend(_missing_block(ice_fields))

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
    if len(raw) < 7:
        raise ValueError(
            "HPK line too short (expected at least 7 chars for station id)"
        )

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

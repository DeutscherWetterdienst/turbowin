from dataclasses import dataclass
from pathlib import Path

from format101.bitstream import read_bits
from format101.codec6 import decode_turbowin_text_to_octets
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
    treat_all_ones_as_missing: bool = True,
) -> tuple[int, DecodedField]:
    key = key_override if key_override is not None else _field_key(desc)
    bits_offset, raw = read_bits(
        octets,
        bits_offset,
        desc.nbits,
        treat_all_ones_as_missing=treat_all_ones_as_missing,
    )
    if raw is None:
        return bits_offset, DecodedField(key=key, value=None)

    val = raw * desc.factor + desc.offset
    if desc.factor >= 1 and float(val).is_integer():
        value: float | int = int(val)
    else:
        value = float(val)
    return bits_offset, DecodedField(key=key, value=value)


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
    vis_fields = pilote[idx_vis_fields_start:idx_vis_fields_end]
    if len(vis_fields) != VISUAL_COUNT:
        raise ValueError("Invalid pilote file: insufficient visual fields after 410000")

    idx_wave_marker = idx_vis_fields_end
    wave_marker = pilote[idx_wave_marker]
    if wave_marker.bufr != "408000":
        raise ValueError(
            f"Invalid pilote file: expected 408000 wave marker, got {wave_marker.bufr}"
        )

    idx_wave_fields_start = idx_wave_marker + 1
    idx_wave_fields_end = idx_wave_fields_start + WAVE_COUNT
    wave_fields = pilote[idx_wave_fields_start:idx_wave_fields_end]
    if len(wave_fields) != WAVE_COUNT:
        raise ValueError(
            "Invalid pilote file: insufficient wave fields after first 408000"
        )

    idx_ice_marker = idx_wave_fields_end
    ice_marker = pilote[idx_ice_marker]
    if ice_marker.bufr != "408000":
        raise ValueError(
            f"Invalid pilote file: expected 408000 ice marker, got {ice_marker.bufr}"
        )

    idx_ice_fields_start = idx_ice_marker + 1
    idx_ice_fields_end = idx_ice_fields_start + ICE_COUNT
    ice_fields = pilote[idx_ice_fields_start:idx_ice_fields_end]
    if len(ice_fields) != ICE_COUNT:
        raise ValueError(
            "Invalid pilote file: insufficient ice fields after second 408000"
        )

    return (
        main,
        vis_marker,
        vis_fields,
        wave_marker,
        wave_fields,
        ice_marker,
        ice_fields,
    )


def _decode_fields_variable_length(
    octets: bytes, pilote: list[PilotEntry]
) -> list[DecodedField]:
    """
    Decode a variable-length S-AWS-101 payload:
    - main block is always present
    - group markers (410000, 408000, 408000) control whether the subsequent blocks are present
    - if a marker is 0, the block is not present in the bitstream and must be skipped,
      but we still emit the corresponding fields as missing for stable output alignment

    Note: The legacy "all-ones means missing" convention must be disabled for 1-bit marker fields.
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

    def decode_block(entries: list[PilotEntry]) -> list[DecodedField]:
        nonlocal bits_offset
        res: list[DecodedField] = []
        for e in entries:
            if have_bits(e.nbits):
                bits_offset, f = _decode_entry(octets, bits_offset, e)
                res.append(f)
            else:
                res.append(DecodedField(key=_field_key(e), value=None))
        return res

    def decode_marker(entry: PilotEntry, key: str) -> int | None:
        nonlocal bits_offset
        if not have_bits(entry.nbits):
            out.append(DecodedField(key=key, value=None))
            return None
        bits_offset, f = _decode_entry(
            octets,
            bits_offset,
            entry,
            key_override=key,
            treat_all_ones_as_missing=False,
        )
        out.append(f)
        if f.value is None:
            return None
        return int(f.value)

    # main block
    out.extend(decode_block(main))

    # visual marker + fields
    vis_val = decode_marker(vis_marker, "410000_visual")
    if vis_val == 1:
        out.extend(decode_block(vis_fields))
    else:
        out.extend(_missing_block(vis_fields))

    # wave marker + fields
    wave_val = decode_marker(wave_marker, "408000_wave")
    if wave_val == 1:
        out.extend(decode_block(wave_fields))
    else:
        out.extend(_missing_block(wave_fields))

    # ice marker + fields
    ice_val = decode_marker(ice_marker, "408000_ice")
    if ice_val == 1:
        out.extend(decode_block(ice_fields))
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

    payload_text = raw[7:].encode("latin1")
    octets = decode_turbowin_text_to_octets(payload_text)

    # Skip 000000 operating mode (not part of payload)
    if pilote and pilote[0].bufr == "000000" and pilote[0].ref == "":
        pilote = pilote[1:]

    fields = _decode_fields_variable_length(octets, pilote)

    return DecodedMessage(
        station_id_raw=prefix,
        station_id=station_id,
        template=template,
        fields=tuple(fields),
    )

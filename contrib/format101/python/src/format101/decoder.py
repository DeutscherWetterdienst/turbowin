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


def _field_key(desc: PilotEntry) -> str:
    return f"{desc.bufr}{('_' + desc.ref) if desc.ref else ''}"


def _decode_entry(
    octets: bytes, b_ofs: int, desc: PilotEntry
) -> tuple[int, DecodedField]:
    key = _field_key(desc)
    b_ofs, raw = read_bits(octets, b_ofs, desc.nbits)
    if raw is None:
        return b_ofs, DecodedField(key=key, value=None)

    val = raw * desc.factor + desc.offset
    if desc.factor >= 1 and float(val).is_integer():
        value: float | int = int(val)
    else:
        value = float(val)
    return b_ofs, DecodedField(key=key, value=value)


def _decode_block(
    octets: bytes, b_ofs: int, entries: list[PilotEntry]
) -> tuple[int, list[DecodedField]]:
    out: list[DecodedField] = []
    for e in entries:
        b_ofs, field = _decode_entry(octets, b_ofs, e)
        out.append(field)
    return b_ofs, out


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
    idx_vis_fields_end = idx_vis_fields_start + 10
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
    idx_wave_fields_end = idx_wave_fields_start + 8
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
    idx_ice_fields_end = idx_ice_fields_start + 8
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

    If a replication indicator is 0 (missing/absent), the subsequent group fields are
    not present in the bitstream and must be skipped.
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
    b_ofs = 0
    n_total_bits = len(octets) * 8

    def ensure_bits(nbits_needed: int) -> bool:
        return b_ofs + nbits_needed <= n_total_bits

    # main block
    if not ensure_bits(sum(e.nbits for e in main)):
        # Payload too short: fall back to linear missing fill
        _, fields_main = (
            _decode_block(octets, b_ofs, main)
            if n_total_bits > 0
            else (b_ofs, _missing_block(main))
        )
        out.extend(fields_main)
        out.append(DecodedField(key=_field_key(vis_marker), value=None))
        out.extend(_missing_block(vis_fields))
        out.append(DecodedField(key=_field_key(wave_marker), value=None))
        out.extend(_missing_block(wave_fields))
        out.append(DecodedField(key=_field_key(ice_marker), value=None))
        out.extend(_missing_block(ice_fields))
        return out

    b_ofs, fields_main = _decode_block(octets, b_ofs, main)
    out.extend(fields_main)

    # visual marker + block
    if not ensure_bits(vis_marker.nbits):
        out.append(DecodedField(key=_field_key(vis_marker), value=None))
        out.extend(_missing_block(vis_fields))
        out.append(DecodedField(key=_field_key(wave_marker), value=None))
        out.extend(_missing_block(wave_fields))
        out.append(DecodedField(key=_field_key(ice_marker), value=None))
        out.extend(_missing_block(ice_fields))
        return out

    b_ofs, vis_m = _decode_entry(octets, b_ofs, vis_marker)
    out.append(vis_m)
    vis_rep = 0 if vis_m.value is None else int(vis_m.value)

    if vis_rep == 1:
        if ensure_bits(sum(e.nbits for e in vis_fields)):
            b_ofs, vis_dec = _decode_block(octets, b_ofs, vis_fields)
            out.extend(vis_dec)
        else:
            out.extend(_missing_block(vis_fields))
    else:
        out.extend(_missing_block(vis_fields))

    # wave marker + block
    if not ensure_bits(wave_marker.nbits):
        out.append(DecodedField(key=_field_key(wave_marker), value=None))
        out.extend(_missing_block(wave_fields))
        out.append(DecodedField(key=_field_key(ice_marker), value=None))
        out.extend(_missing_block(ice_fields))
        return out

    b_ofs, wave_m = _decode_entry(octets, b_ofs, wave_marker)
    out.append(wave_m)
    wave_rep = 0 if wave_m.value is None else int(wave_m.value)

    if wave_rep == 1:
        if ensure_bits(sum(e.nbits for e in wave_fields)):
            b_ofs, wave_dec = _decode_block(octets, b_ofs, wave_fields)
            out.extend(wave_dec)
        else:
            out.extend(_missing_block(wave_fields))
    else:
        out.extend(_missing_block(wave_fields))

    # ice marker + block
    if not ensure_bits(ice_marker.nbits):
        out.append(DecodedField(key=_field_key(ice_marker), value=None))
        out.extend(_missing_block(ice_fields))
        return out

    b_ofs, ice_m = _decode_entry(octets, b_ofs, ice_marker)
    out.append(ice_m)
    ice_rep = 0 if ice_m.value is None else int(ice_m.value)

    if ice_rep == 1:
        if ensure_bits(sum(e.nbits for e in ice_fields)):
            b_ofs, ice_dec = _decode_block(octets, b_ofs, ice_fields)
            out.extend(ice_dec)
        else:
            out.extend(_missing_block(ice_fields))
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

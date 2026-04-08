from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

from format101.bitstream import write_bits
from format101.codec6 import encode_payload_octets_to_turbowin_text
from format101.spec import PilotEntry, load_pilote_csv


@dataclass(frozen=True)
class EncodedMessage:
    station_id_raw: str
    station_id: str
    template: str
    payload_text: bytes
    payload_octets: bytes
    payload_bits: int

    def to_hpk_line(self) -> str:
        return self.station_id_raw + self.payload_text.decode("latin1")


def parse_format101_txt(path: Path) -> list[tuple[bool, float | None]]:
    """
    Parse TurboWin-style format_101.txt input.

    Format:
    - first line: "0" (operating mode)
    - then lines like:
      - "0" (missing)
      - "1 <value>" (present)
    Comments may follow.

    Returns a list aligned to the pilote CSV fields (excluding the pilote's 000000 entry).
    """
    lines = path.read_text(encoding="utf-8").splitlines()
    if not lines:
        raise ValueError(f"Empty format_101.txt: {path}")
    if lines[0].strip() != "0":
        raise ValueError(f"Invalid first line in {path} (expected '0'): {lines[0]!r}")

    parsed: list[tuple[bool, float | None]] = []
    for raw in lines[1:]:
        s = raw.strip()
        if not s:
            continue
        parts = s.split()
        if parts[0] == "0":
            parsed.append((False, None))
        elif parts[0] == "1":
            if len(parts) < 2:
                raise ValueError(
                    f"Invalid present line (missing value): {raw!r} in {path}"
                )
            parsed.append((True, float(parts[1])))
        else:
            raise ValueError(f"Invalid line (expected 0/1): {raw!r} in {path}")
    return parsed


def _quantize(entry: PilotEntry, value: float) -> int:
    """
    Quantize a physical value into a raw coded integer.
    - round to nearest integer
    - clamp to [0, codmax]
    """
    if entry.factor == 0:
        raw = 0
    else:
        raw = round((value - entry.offset) / entry.factor)

    if raw < 0:
        raw = 0
    if raw > entry.codmax:
        raw = entry.codmax
    return int(raw)


VISUAL_COUNT = 10
WAVE_COUNT = 8
ICE_COUNT = 8


def encode_format101_from_txt(
    *,
    format101_txt: str | Path,
    pilote_csv: str | Path,
    station_id: str,
    template: str = "S-AWS-101",
) -> EncodedMessage:
    """
    Encode a TurboWin+ format_101.txt into a single HPK line (station id prefix + payload text).

    This implementation targets 1:1 compatibility with TurboWin+ legacy vectors:
    - uses the legacy pilote file (miscellaneous/format_101/config/)
    - applies legacy MISSING convention: all bits set to 1 for a field
    - encodes a variable-length payload based on a chained replication marker scheme

    Observed behavior for legacy vectors indicates that the marker bits form a chain:
      - 410000: visual group present
      - first 408000: at least one group after visual is present (wave or ice)
      - second 408000: ice group present
    """
    station_id = station_id.strip()
    if not (1 <= len(station_id) <= 7):
        raise ValueError("station_id must be 1..7 characters")
    station_id_raw = station_id.rjust(7, " ")

    pilote = load_pilote_csv(pilote_csv)

    # The pilote includes an initial 000000 "operating mode" entry which is not part of the payload.
    if pilote and pilote[0].bufr == "000000" and pilote[0].ref == "":
        pilote = pilote[1:]

    values = parse_format101_txt(Path(format101_txt))
    if len(values) != len(pilote):
        raise ValueError(
            f"Input value line count mismatch: expected {len(pilote)} entries, got {len(values)}"
        )

    # Locate section boundaries in legacy pilote (after skipping 000000)
    idx_022042 = next(i for i, e in enumerate(pilote) if e.bufr == "022042")
    idx_vis_marker = idx_022042 + 1
    idx_vis_fields_start = idx_vis_marker + 1
    idx_vis_fields_end = idx_vis_fields_start + VISUAL_COUNT

    idx_chain_marker = idx_vis_fields_end  # first 408000 in legacy pilote
    idx_wave_fields_start = idx_chain_marker + 1
    idx_wave_fields_end = idx_wave_fields_start + WAVE_COUNT

    idx_ice_marker = idx_wave_fields_end  # second 408000 in legacy pilote
    idx_ice_fields_start = idx_ice_marker + 1
    idx_ice_fields_end = idx_ice_fields_start + ICE_COUNT

    def group_has_any_present(start: int, end: int) -> bool:
        for i in range(start, end):
            present, _v = values[i]
            if present:
                return True
        return False

    has_visual = group_has_any_present(idx_vis_fields_start, idx_vis_fields_end)
    has_wave = group_has_any_present(idx_wave_fields_start, idx_wave_fields_end)
    has_ice = group_has_any_present(idx_ice_fields_start, idx_ice_fields_end)

    # Derive marker chain values (override whatever is in format_101.txt)
    m_visual = 1 if has_visual else 0
    m_chain = 1 if (has_wave or has_ice) else 0
    m_ice = 1 if has_ice else 0

    out = bytearray()
    b_ofs = 0

    def encode_entry(i: int, *, force_missing: bool = False) -> None:
        nonlocal b_ofs
        entry = pilote[i]
        present, value = values[i]

        if force_missing:
            raw = (1 << entry.nbits) - 1
        else:
            if not present:
                raw = (1 << entry.nbits) - 1
            else:
                assert value is not None
                raw = _quantize(entry, value)

        b_ofs = write_bits(out, b_ofs, entry.nbits, raw)

    def encode_marker(i: int, marker_val: int) -> None:
        nonlocal b_ofs
        entry = pilote[i]
        raw = int(marker_val) & ((1 << entry.nbits) - 1)
        b_ofs = write_bits(out, b_ofs, entry.nbits, raw)

    # main block (0 .. idx_022042)
    for i in range(0, idx_022042 + 1):
        encode_entry(i)

    # visual marker (410000)
    encode_marker(idx_vis_marker, m_visual)
    if has_visual:
        for i in range(idx_vis_fields_start, idx_vis_fields_end):
            encode_entry(i)
    else:
        # still write placeholder fields (missing) to keep bitstream aligned
        for i in range(idx_vis_fields_start, idx_vis_fields_end):
            encode_entry(i, force_missing=True)

    # chain marker (first 408000)
    encode_marker(idx_chain_marker, m_chain)

    if m_chain == 1:
        # wave fields (may be missing)
        if has_wave:
            for i in range(idx_wave_fields_start, idx_wave_fields_end):
                encode_entry(i)
        else:
            for i in range(idx_wave_fields_start, idx_wave_fields_end):
                encode_entry(i, force_missing=True)

        # ice marker (second 408000)
        encode_marker(idx_ice_marker, m_ice)

        # ice fields (may be missing)
        if has_ice:
            for i in range(idx_ice_fields_start, idx_ice_fields_end):
                encode_entry(i)
        else:
            for i in range(idx_ice_fields_start, idx_ice_fields_end):
                encode_entry(i, force_missing=True)

    payload_bits = b_ofs
    payload_octets = bytes(out[: (b_ofs + 7) // 8])
    payload_text = encode_payload_octets_to_turbowin_text(payload_octets)

    return EncodedMessage(
        station_id_raw=station_id_raw,
        station_id=station_id,
        template=template,
        payload_text=payload_text,
        payload_octets=payload_octets,
        payload_bits=payload_bits,
    )

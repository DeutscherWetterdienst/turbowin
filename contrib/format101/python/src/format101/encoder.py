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
    - produces a variable-length message based on group marker bits (410000 / 408000 / 408000)
    - applies legacy MISSING convention: all bits set to 1 for a field
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

    # Find marker indices in the legacy pilote file
    idx_visual = next(i for i, e in enumerate(pilote) if e.bufr == "410000")
    idx_wave = next(i for i, e in enumerate(pilote) if e.bufr == "408000")
    idx_ice = next(
        i for i, e in enumerate(pilote) if e.bufr == "408000" and i > idx_wave
    )

    # Determine whether the optional blocks should be emitted (variable length)
    visual_emit = bool(values[idx_visual][0] and int(values[idx_visual][1] or 0) == 1)
    wave_emit = bool(values[idx_wave][0] and int(values[idx_wave][1] or 0) == 1)
    ice_emit = bool(values[idx_ice][0] and int(values[idx_ice][1] or 0) == 1)

    out = bytearray()
    b_ofs = 0

    for i, entry in enumerate(pilote):
        # Skip full blocks that are not present in the message bitstream
        if i > idx_visual:
            if idx_visual < i < idx_wave and not visual_emit:
                continue
            if idx_wave < i < idx_ice and not wave_emit:
                continue
            if i > idx_ice and not ice_emit:
                continue

        present, value = values[i]

        if not present:
            raw = (1 << entry.nbits) - 1
        else:
            assert value is not None
            raw = _quantize(entry, value)

        b_ofs = write_bits(out, b_ofs, entry.nbits, raw)

    payload_octets = bytes(out)
    payload_text = encode_payload_octets_to_turbowin_text(payload_octets)

    return EncodedMessage(
        station_id_raw=station_id_raw,
        station_id=station_id,
        template=template,
        payload_text=payload_text,
    )

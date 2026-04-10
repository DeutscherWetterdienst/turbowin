from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

from format101.bitstream import write_bits
from format101.codec6 import encode_payload_bits_to_turbowin_text
from format101.spec import PilotEntry, load_pilote_csv


@dataclass(frozen=True)
class EncodedMessage:
    station_id_raw: str
    station_id: str
    template: str
    payload_text: bytes
    payload_octets: bytes
    payload_bits: int
    unfinalized_octets: bytes
    unfinalized_bits: int

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
    finalize: bool = True,
) -> EncodedMessage:
    """
    Encode a TurboWin+ format_101.txt into a single HPK line (station id prefix + payload text).

    This implementation aims to match the observed behavior of the TurboWin+ legacy
    reference encoder (`teste_hc_TW.exe`) for S-AWS-101 (format #101).

    Layout (after skipping pilote entry 000000):
    - main block up to 022042 (inclusive)
    - 410000 visual marker:
        0 => stop
        1 => write 10 visual fields
    - first 408000 (chain marker):
        0 => stop
        1 => write 8 wave fields, then second 408000 (ice marker)
    - second 408000 (ice marker):
        0 => stop
        1 => write 8 ice fields

    The `finalize` flag exists to support black-box inference of the exact reference
    padding/termination behavior. In normal operation it should remain True.
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

    def read_marker(idx: int) -> int:
        present, v = values[idx]
        if not present or v is None:
            return 0
        return 1 if int(v) != 0 else 0

    m_visual = read_marker(idx_vis_marker)
    m_chain = read_marker(idx_chain_marker)
    m_ice = read_marker(idx_ice_marker)

    out = bytearray()
    b_ofs = 0

    def encode_entry(i: int) -> None:
        nonlocal b_ofs
        entry = pilote[i]
        present, value = values[i]
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

    def finalize_bitstream() -> None:
        """
        Final padding to match the reference behavior (inferred by solve_padding.py).

        Rules:
        - If b_ofs % 8 == 4, append two 1-bits ("11")
        - If b_ofs % 8 == 2, append one 1-bit ("1")
        - Then byte-align by appending 0-bits as needed
        """
        nonlocal b_ofs
        r = b_ofs % 8
        if r == 4:
            b_ofs = write_bits(out, b_ofs, 2, 0b11)
        elif r == 2:
            b_ofs = write_bits(out, b_ofs, 1, 0b1)

        pad8 = (-b_ofs) % 8
        if pad8:
            b_ofs = write_bits(out, b_ofs, pad8, 0)

    def build_message() -> EncodedMessage:
        unfinalized_bits = b_ofs
        unfinalized_octets = bytes(out[: (b_ofs + 7) // 8])

        if finalize:
            finalize_bitstream()

        payload_bits = b_ofs
        payload_octets = bytes(out[: (b_ofs + 7) // 8])

        # IMPORTANT: The TurboWin+ payload is fundamentally a 6-bit word stream.
        # Encoding via octets -> 6-bit words is ambiguous and can diverge from the reference.
        payload_text = encode_payload_bits_to_turbowin_text(out, payload_bits)

        return EncodedMessage(
            station_id_raw=station_id_raw,
            station_id=station_id,
            template=template,
            payload_text=payload_text,
            payload_octets=payload_octets,
            payload_bits=payload_bits,
            unfinalized_octets=unfinalized_octets,
            unfinalized_bits=unfinalized_bits,
        )

    # main block (0 .. idx_022042)
    for i in range(0, idx_022042 + 1):
        encode_entry(i)

    # visual marker (410000)
    encode_marker(idx_vis_marker, m_visual)
    if m_visual == 0:
        return build_message()

    # visual fields
    for i in range(idx_vis_fields_start, idx_vis_fields_end):
        encode_entry(i)

    # chain marker (first 408000)
    encode_marker(idx_chain_marker, m_chain)
    if m_chain == 0:
        return build_message()

    # wave fields
    for i in range(idx_wave_fields_start, idx_wave_fields_end):
        encode_entry(i)

    # ice marker (second 408000)
    encode_marker(idx_ice_marker, m_ice)
    if m_ice == 0:
        return build_message()

    # ice fields
    for i in range(idx_ice_fields_start, idx_ice_fields_end):
        encode_entry(i)

    return build_message()

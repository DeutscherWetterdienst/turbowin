from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


@dataclass(frozen=True)
class PilotEntry:
    bufr: str
    ref: str
    nbits: int
    factor: float
    offset: float


def _parse_float(s: str) -> float:
    s = s.strip()
    if not s:
        return 0.0
    return float(s)


def load_pilote_csv(path: str | Path) -> list[PilotEntry]:
    p = Path(path)
    entries: list[PilotEntry] = []
    for raw_line in p.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        cols = [c.strip() for c in line.split(";")]
        # Expected columns (MAWSconfig/MAWSbin style):
        # 0 BUFR; 1 ref; 2 nbits; 3 degre; 4 factor; 5 offset; ...
        if len(cols) < 6:
            raise ValueError(
                f"Invalid pilote csv line (expected >= 6 columns): {raw_line!r}"
            )
        bufr = cols[0]
        ref = cols[1]
        nbits = int(cols[2])
        factor = _parse_float(cols[4])
        offset = _parse_float(cols[5])
        entries.append(
            PilotEntry(bufr=bufr, ref=ref, nbits=nbits, factor=factor, offset=offset)
        )
    return entries


def iter_pilote_fields(entries: Iterable[PilotEntry]) -> list[str]:
    return [f"{e.bufr}{('_' + e.ref) if e.ref else ''}" for e in entries]

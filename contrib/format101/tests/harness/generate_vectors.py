import argparse
import random
import re
from dataclasses import dataclass
from pathlib import Path

from format101.spec import PilotEntry, load_pilote_csv

REPO_ROOT = Path(__file__).resolve().parents[2]

DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)

ID_RE = re.compile(r"^[A-Za-z0-9]{1,7}$")


@dataclass(frozen=True)
class GroupLayout:
    visual_marker_idx: int
    visual_count: int
    wave_marker_idx: int
    wave_count: int
    ice_marker_idx: int
    ice_count: int


LEGACY_LAYOUT = GroupLayout(
    visual_marker_idx=28,
    visual_count=10,
    wave_marker_idx=39,
    wave_count=8,
    ice_marker_idx=48,
    ice_count=8,
)


def station_id_from_counter(n: int) -> str:
    """
    Deterministic station ID generator, producing 1..7 uppercase alnum characters.

    Examples:
      0 -> "A"
      1 -> "B"
      ...
      25 -> "Z"
      26 -> "AA"
    """
    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    if n < 0:
        n = 0
    s = ""
    n0 = n
    while True:
        s = alphabet[n % len(alphabet)] + s
        n = n // len(alphabet) - 1
        if n < 0:
            break
    # clamp length to 7 (TurboWin limit)
    if len(s) > 7:
        s = s[-7:]
    if not s:
        s = "A"
    # Avoid purely numeric IDs (just for readability)
    if s.isdigit():
        s = "A" + s[:6]
    assert ID_RE.match(s), (n0, s)
    return s


def render_format101_txt(
    values: list[tuple[bool, float | None]],
    *,
    include_comments: bool = False,
) -> str:
    """
    Render TurboWin-style format_101.txt:
    - first line "0" (operating mode)
    - then per pilote entry: "0" or "1 <value>"
    """
    lines = ["0"]
    for present, value in values:
        if not present or value is None:
            lines.append("0")
        else:
            # stable formatting; keep as float to match existing vectors
            lines.append(f"1 {value}")
    return "\n".join(lines) + "\n"


def choose_present_value(entry: PilotEntry, *, rng: random.Random) -> float:
    """
    Choose a representable physical value for an entry:
      raw in [0..codmax] -> phys = raw*factor + offset
    """
    if entry.nbits == 0:
        return 0.0

    # Keep values inside range, prefer a few interesting raw values
    interesting = [0, 1, 2, 3, 5, 10, entry.codmax // 2, entry.codmax - 1, entry.codmax]
    interesting = [x for x in interesting if 0 <= x <= entry.codmax]
    raw = rng.choice(interesting) if interesting else rng.randint(0, entry.codmax)

    phys = raw * entry.factor + entry.offset
    return float(phys)


def apply_group_constraints(
    values: list[tuple[bool, float | None]], layout: GroupLayout
) -> None:
    """
    Enforce: if marker value is 0 => all fields in that group are missing (present=False).
    Marker lines themselves remain present with value 0/1.
    """

    def marker_is_zero(idx: int) -> bool:
        present, v = values[idx]
        if not present or v is None:
            return True
        return int(float(v)) == 0

    if marker_is_zero(layout.visual_marker_idx):
        start = layout.visual_marker_idx + 1
        end = start + layout.visual_count
        for i in range(start, end):
            values[i] = (False, None)

    if marker_is_zero(layout.wave_marker_idx):
        start = layout.wave_marker_idx + 1
        end = start + layout.wave_count
        for i in range(start, end):
            values[i] = (False, None)

    if marker_is_zero(layout.ice_marker_idx):
        start = layout.ice_marker_idx + 1
        end = start + layout.ice_count
        for i in range(start, end):
            values[i] = (False, None)


def build_base_values(
    pilote: list[PilotEntry],
    *,
    rng: random.Random,
) -> list[tuple[bool, float | None]]:
    """
    Generate a baseline vector with:
    - most fields missing
    - some core fields present with representable values
    - markers present (0/1), and group values consistent with marker
    """
    values: list[tuple[bool, float | None]] = [(False, None) for _ in pilote]

    # Core mandatory-ish fields: make present with representable values
    # Indices correspond to legacy pilote after skipping 000000.
    core_indices = [
        0,  # 001198 format identifier
        1,  # 001199 callsign encryption indicator
        2,  # 001012 COG
        3,  # 001013 SOG
        5,  # 007072
        6,  # 004001 year
        7,  # 004002 month
        8,  # 004003 day
        9,  # 004004 hour
        10,  # 004005 minute
        11,  # 005002 lat
        12,  # 006002 lon
    ]
    for idx in core_indices:
        e = pilote[idx]
        values[idx] = (True, choose_present_value(e, rng=rng))

    # Force format identifier to 101 if this is legacy S-AWS-101
    if pilote and pilote[0].bufr == "001198":
        values[0] = (True, 101.0)

    # Markers: present with 0/1
    for marker_idx in (
        LEGACY_LAYOUT.visual_marker_idx,
        LEGACY_LAYOUT.wave_marker_idx,
        LEGACY_LAYOUT.ice_marker_idx,
    ):
        values[marker_idx] = (True, float(rng.choice([0, 1])))

    # If marker==1, populate the group fields with some values and some missing
    def fill_group(marker_idx: int, count: int) -> None:
        marker_val = int(float(values[marker_idx][1] or 0))
        start = marker_idx + 1
        end = start + count
        if marker_val == 0:
            return
        for i in range(start, end):
            e = pilote[i]
            # 70% chance to be present
            if rng.random() < 0.7:
                values[i] = (True, choose_present_value(e, rng=rng))
            else:
                values[i] = (False, None)

    fill_group(LEGACY_LAYOUT.visual_marker_idx, LEGACY_LAYOUT.visual_count)
    fill_group(LEGACY_LAYOUT.wave_marker_idx, LEGACY_LAYOUT.wave_count)
    fill_group(LEGACY_LAYOUT.ice_marker_idx, LEGACY_LAYOUT.ice_count)

    # Enforce constraint marker==0 => all fields missing
    apply_group_constraints(values, LEGACY_LAYOUT)
    return values


def build_combo_vectors(
    pilote: list[PilotEntry],
    *,
    count_per_combo: int,
    rng: random.Random,
) -> list[tuple[str, list[tuple[bool, float | None]]]]:
    """
    Build vectors for all 8 combinations of (visual,wave,ice) markers, with deterministic naming.
    """
    out: list[tuple[str, list[tuple[bool, float | None]]]] = []

    combos = []
    for v in (0, 1):
        for w in (0, 1):
            for i in (0, 1):
                combos.append((v, w, i))

    for combo in combos:
        v, w, ic = combo
        for n in range(count_per_combo):
            values = build_base_values(pilote, rng=rng)

            # Set markers explicitly for this combo
            values[LEGACY_LAYOUT.visual_marker_idx] = (True, float(v))
            values[LEGACY_LAYOUT.wave_marker_idx] = (True, float(w))
            values[LEGACY_LAYOUT.ice_marker_idx] = (True, float(ic))

            # If marker==1, ensure at least one value present in the block (to avoid degenerate "all missing")
            def ensure_one_present(marker_idx: int, count: int) -> None:
                marker_val = int(float(values[marker_idx][1] or 0))
                start = marker_idx + 1
                end = start + count
                if marker_val == 0:
                    for j in range(start, end):
                        values[j] = (False, None)
                    return
                # ensure one present
                pick = rng.randrange(start, end)
                values[pick] = (True, choose_present_value(pilote[pick], rng=rng))

            ensure_one_present(
                LEGACY_LAYOUT.visual_marker_idx, LEGACY_LAYOUT.visual_count
            )
            ensure_one_present(LEGACY_LAYOUT.wave_marker_idx, LEGACY_LAYOUT.wave_count)
            ensure_one_present(LEGACY_LAYOUT.ice_marker_idx, LEGACY_LAYOUT.ice_count)

            apply_group_constraints(values, LEGACY_LAYOUT)

            name = f"combo_v{v}w{w}i{ic}_{n + 1:02d}"
            out.append((name, values))

    return out


def build_random_vectors(
    pilote: list[PilotEntry],
    *,
    count: int,
    rng: random.Random,
) -> list[tuple[str, list[tuple[bool, float | None]]]]:
    out: list[tuple[str, list[tuple[bool, float | None]]]] = []
    for n in range(count):
        values = build_base_values(pilote, rng=rng)
        name = f"random_{n + 1:04d}"
        out.append((name, values))
    return out


def main() -> int:
    ap = argparse.ArgumentParser(
        description="Generate TurboWin+ legacy format_101.txt test vectors"
    )
    ap.add_argument(
        "--pilote",
        type=Path,
        default=DEFAULT_PILOTE,
        help="Pilot CSV to use (default: TurboWin+ legacy copy)",
    )
    ap.add_argument(
        "--out-dir",
        type=Path,
        required=True,
        help="Output directory for generated *.format_101.txt vectors",
    )
    ap.add_argument(
        "--mode",
        choices=["combo", "random"],
        default="combo",
        help="Generation mode",
    )
    ap.add_argument(
        "--count",
        type=int,
        default=100,
        help="Number of vectors (random mode) or vectors per combo (combo mode uses --per-combo)",
    )
    ap.add_argument(
        "--per-combo",
        type=int,
        default=1,
        help="Number of vectors to generate per marker combination (combo mode)",
    )
    ap.add_argument(
        "--seed",
        type=int,
        default=1234,
        help="Random seed",
    )
    ap.add_argument(
        "--prefix",
        default="generated",
        help="Filename prefix",
    )
    args = ap.parse_args()

    out_dir: Path = args.out_dir
    out_dir.mkdir(parents=True, exist_ok=True)

    rng = random.Random(args.seed)

    pilote = load_pilote_csv(args.pilote)
    if pilote and pilote[0].bufr == "000000" and pilote[0].ref == "":
        pilote = pilote[1:]

    if len(pilote) < 57:
        raise SystemExit(
            f"Pilote file too short for expected legacy layout (need >= 57 entries, got {len(pilote)})"
        )

    items: list[tuple[str, list[tuple[bool, float | None]]]]
    if args.mode == "combo":
        items = build_combo_vectors(pilote, count_per_combo=args.per_combo, rng=rng)
    else:
        items = build_random_vectors(pilote, count=args.count, rng=rng)

    written = 0
    for idx, (name, values) in enumerate(items):
        station_id = station_id_from_counter(idx)
        fn = out_dir / f"{args.prefix}_{name}_id{station_id}.format_101.txt"
        txt = render_format101_txt(values)
        fn.write_text(txt, encoding="utf-8")
        written += 1

    print(f"Wrote {written} vector(s) to {out_dir}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

import math
import re
from dataclasses import dataclass
from pathlib import Path

from format101.decoder import decode_hpk_line
from format101.spec import pilote_key, load_pilote_csv

REPO_ROOT = Path(__file__).resolve().parents[2]

VECTORS_DIR = REPO_ROOT / "tests" / "vectors"
DEFAULT_PILOTE = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)

ID_RE = re.compile(r"_id([A-Za-z0-9]{1,7})$")


@dataclass(frozen=True)
class ParsedLine:
    present: bool
    value: float | None


def extract_identifier(stem: str) -> str:
    m = ID_RE.search(stem)
    if not m:
        raise ValueError(
            f"Cannot extract identifier from filename stem '{stem}'. "
            "Expected pattern '*_id<STATIONID>.format_101.txt' where STATIONID is 1..7 chars."
        )
    return m.group(1)


def parse_format101_txt(path: Path) -> list[ParsedLine]:
    """
    Parse TurboWin-style format_101.txt input.

    Format:
    - first line: "0" (operating mode)
    - then lines like:
      - "0" (missing)
      - "1 <value>" (present)
    Comments may follow.
    """
    lines = path.read_text(encoding="utf-8").splitlines()
    if not lines:
        raise ValueError(f"Empty format_101.txt: {path}")
    if lines[0].strip() != "0":
        raise ValueError(f"Invalid first line in {path} (expected '0'): {lines[0]!r}")

    parsed: list[ParsedLine] = []
    for raw in lines[1:]:
        s = raw.strip()
        if not s:
            continue
        parts = s.split()
        if parts[0] == "0":
            parsed.append(ParsedLine(present=False, value=None))
        elif parts[0] == "1":
            if len(parts) < 2:
                raise ValueError(
                    f"Invalid present line (missing value): {raw!r} in {path}"
                )
            parsed.append(ParsedLine(present=True, value=float(parts[1])))
        else:
            raise ValueError(f"Invalid line (expected 0/1): {raw!r} in {path}")
    return parsed


def format101_tolerance(factor: float) -> float:
    """
    Tolerance used to compare decoded values against the original TurboWin input.

    For quantized fields:
      encoded = round((phys - offset) / factor)
      decoded = encoded * factor + offset

    Therefore the maximum absolute error to the nearest representable value is factor/2.
    """
    if factor <= 0:
        return 0.0
    return factor / 2.0 + 1e-12


def almost_equal(a: float, b: float, tol: float) -> bool:
    return math.isfinite(a) and math.isfinite(b) and abs(a - b) <= tol


def main() -> int:
    pilote_all = load_pilote_csv(DEFAULT_PILOTE)

    # TurboWin format_101.txt does not contain a data line for the first pilote entry
    # (000000 = operating mode), because the operating mode is provided as the first
    # line in format_101.txt ("0"). Therefore we skip it for comparisons.
    pilote = [e for e in pilote_all if not (e.bufr == "000000" and e.ref == "")]

    pilote_keys = [pilote_key(e) for e in pilote]
    pilote_by_key = {pilote_key(e): e for e in pilote}

    ok = True

    for input_path in sorted(VECTORS_DIR.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        expected_hpk = VECTORS_DIR / f"{stem}.expected.hpk.txt"
        if not expected_hpk.exists():
            print(f"[FAIL] Missing expected file: {expected_hpk}")
            ok = False
            continue

        identifier = extract_identifier(stem)

        # Parse expected inputs
        inp = parse_format101_txt(input_path)
        if len(inp) != len(pilote_keys):
            print(f"[FAIL] {stem}: input line count mismatch")
            print(f"  expected {len(pilote_keys)} data lines, got {len(inp)}")
            ok = False
            continue

        # Decode expected HPK
        hpk_line = expected_hpk.read_text(encoding="latin1").splitlines()[0]
        msg = decode_hpk_line(hpk_line, pilote_csv=DEFAULT_PILOTE)
        decoded = {f.key: f.value for f in msg.fields}

        # Station id prefix check
        prefix_expected = identifier.rjust(7, " ")
        if msg.station_id_raw != prefix_expected:
            print(f"[FAIL] {stem}: station id prefix mismatch")
            print(f"  expected raw prefix: {prefix_expected!r}")
            print(f"  got raw prefix     : {msg.station_id_raw!r}")
            ok = False

        # Field-by-field comparison
        for idx, key in enumerate(pilote_keys):
            entry = pilote_by_key[key]
            expected_present = inp[idx].present
            expected_value = inp[idx].value

            got_value = decoded.get(key, None)
            got_present = got_value is not None

            if expected_present != got_present:
                ok = False
                print(f"[FAIL] {stem}: presence mismatch at idx={idx} key={key}")
                print(f"  expected present: {expected_present}")
                print(f"  got present     : {got_present}")
                print(f"  expected value  : {expected_value}")
                print(f"  got value       : {got_value}")
                continue

            if not expected_present:
                continue

            assert expected_value is not None

            if not isinstance(got_value, (int, float)):
                ok = False
                print(
                    f"[FAIL] {stem}: decoded value type mismatch at idx={idx} key={key}"
                )
                print(f"  expected numeric, got: {type(got_value)}")
                continue

            got_f = float(got_value)
            tol = format101_tolerance(entry.factor)

            if not almost_equal(expected_value, got_f, tol):
                ok = False
                print(f"[FAIL] {stem}: value mismatch at idx={idx} key={key}")
                print(f"  factor  : {entry.factor}")
                print(f"  offset  : {entry.offset}")
                print(f"  tol     : {tol}")
                print(f"  expected: {expected_value}")
                print(f"  got     : {got_f}")

        if ok:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

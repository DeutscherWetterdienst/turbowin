import math
import re
from dataclasses import dataclass
from pathlib import Path

from format101.decoder import decode_hpk_line
from format101.spec import pilote_key, load_pilote_csv, PilotEntry

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

    Note: In TurboWin, some fields are marked present in format_101.txt but are encoded
    as MISSING in the reference binary output (e.g. 001199 in the legacy vectors).
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


def normalize_expected_value(entry: PilotEntry, input_value: float) -> float:
    """
    Normalize an input value to the value representable after encoding + decoding.

    The reference encoder quantizes and also clamps to the representable raw range.
    """
    if entry.factor == 0:
        return entry.offset

    raw = round((input_value - entry.offset) / entry.factor)

    if raw < 0:
        raw = 0
    if raw > entry.codmax:
        raw = entry.codmax

    return raw * entry.factor + entry.offset


def main() -> int:
    pilote_all = load_pilote_csv(DEFAULT_PILOTE)

    # TurboWin format_101.txt does not contain a data line for the first pilote entry
    # (000000 = operating mode), because the operating mode is provided as the first
    # line in format_101.txt ("0"). Therefore we skip it for comparisons.
    pilote = [e for e in pilote_all if not (e.bufr == "000000" and e.ref == "")]

    pilote_keys = [pilote_key(e) for e in pilote]
    pilote_by_key = {pilote_key(e): e for e in pilote}

    # Fields that are known to be forced/mangled by the reference encoder/decoder pipeline
    # in the legacy TurboWin vectors:
    # - 001199: a 1-bit field where value 1 collides with the legacy MISSING convention
    # - 410000 / 408000: group markers are handled via special keys in our decoder implementation
    IGNORE_KEYS = {"001199", "410000", "408000"}

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
            if key in IGNORE_KEYS:
                continue

            entry = pilote_by_key[key]
            expected_present = inp[idx].present
            expected_value_input = inp[idx].value

            got_value = decoded.get(key, None)
            got_present = got_value is not None

            if expected_present and got_value is None:
                ok = False
                print(f"[FAIL] {stem}: presence mismatch at idx={idx} key={key}")
                print("  expected present: True")
                print("  got present     : False")
                print(f"  expected value  : {expected_value_input}")
                print(f"  got value       : {got_value}")
                continue

            if not expected_present and got_present:
                ok = False
                print(f"[FAIL] {stem}: presence mismatch at idx={idx} key={key}")
                print("  expected present: False")
                print("  got present     : True")
                print(f"  expected value  : {expected_value_input}")
                print(f"  got value       : {got_value}")
                continue

            if not expected_present:
                continue

            assert expected_value_input is not None

            if not isinstance(got_value, (int, float)):
                ok = False
                print(
                    f"[FAIL] {stem}: decoded value type mismatch at idx={idx} key={key}"
                )
                print(f"  expected numeric, got: {type(got_value)}")
                continue

            got_f = float(got_value)
            expected_value_norm = normalize_expected_value(entry, expected_value_input)
            tol = format101_tolerance(entry.factor)

            if not almost_equal(expected_value_norm, got_f, tol):
                ok = False
                print(f"[FAIL] {stem}: value mismatch at idx={idx} key={key}")
                print(f"  factor         : {entry.factor}")
                print(f"  offset         : {entry.offset}")
                print(f"  codmax(raw)    : {entry.codmax}")
                print(f"  tol            : {tol}")
                print(f"  expected(input): {expected_value_input}")
                print(f"  expected(norm) : {expected_value_norm}")
                print(f"  got            : {got_f}")

        # Group marker checks (by special keys produced by our decoder)
        if "410000_visual" in decoded:
            exp_visual = inp[pilote_keys.index("410000")].value
            got_visual = decoded["410000_visual"]
            if exp_visual is not None and got_visual is not None:
                if int(exp_visual) != int(got_visual):
                    ok = False
                    print(f"[FAIL] {stem}: marker mismatch 410000_visual")
                    print(f"  expected: {int(exp_visual)}")
                    print(f"  got     : {int(got_visual)}")

        if "408000_wave" in decoded:
            exp_wave = inp[pilote_keys.index("408000")].value
            got_wave = decoded["408000_wave"]
            if exp_wave is not None and got_wave is not None:
                if int(exp_wave) != int(got_wave):
                    ok = False
                    print(f"[FAIL] {stem}: marker mismatch 408000_wave")
                    print(f"  expected: {int(exp_wave)}")
                    print(f"  got     : {int(got_wave)}")

        # Second 408000 is for ice; find its second occurrence in pilote_keys
        if "408000_ice" in decoded:
            first_idx = pilote_keys.index("408000")
            second_idx = pilote_keys.index("408000", first_idx + 1)
            exp_ice = inp[second_idx].value
            got_ice = decoded["408000_ice"]
            if exp_ice is not None and got_ice is not None:
                if int(exp_ice) != int(got_ice):
                    ok = False
                    print(f"[FAIL] {stem}: marker mismatch 408000_ice")
                    print(f"  expected: {int(exp_ice)}")
                    print(f"  got     : {int(got_ice)}")

        if ok:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

import argparse
import re
from pathlib import Path

from run_reference_encoder_windows import REPO_ROOT, run_one

ID_RE = re.compile(r"_id([A-Za-z0-9]{1,7})$")

DEFAULT_SKIP = {
    # Invalid/undefined behavior (reference encoder produces implausibly large output)
    "exp_visual_m0_vv1_idVISM001",
}


def extract_identifier(stem: str) -> str:
    m = ID_RE.search(stem)
    if not m:
        raise ValueError(
            f"Cannot extract identifier from filename stem '{stem}'. "
            "Expected pattern '*_id<STATIONID>.format_101.txt' where STATIONID is 1..7 chars."
        )
    return m.group(1)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dir",
        type=Path,
        default=REPO_ROOT / "tests" / "vectors",
        help="Directory containing *.format_101.txt vectors (default: tests/vectors)",
    )
    ap.add_argument(
        "--skip",
        action="append",
        default=[],
        help="Skip vector stem (without extension); can be provided multiple times",
    )
    args = ap.parse_args()

    skip = set(DEFAULT_SKIP)
    skip.update(args.skip)

    vectors_dir = args.dir
    work_dir = REPO_ROOT / "tests" / ".work"
    work_dir.mkdir(parents=True, exist_ok=True)

    ok = True

    for input_path in sorted(vectors_dir.glob("*.format_101.txt")):
        stem = input_path.stem.replace(".format_101", "")
        if stem in skip:
            print(f"[SKIP] {stem}")
            continue

        expected_path = vectors_dir / f"{stem}.expected.hpk.txt"
        if not expected_path.exists():
            print(f"[FAIL] Missing expected file: {expected_path}")
            ok = False
            continue

        identifier = extract_identifier(stem)
        got = run_one(input_path, identifier).rstrip("\n")
        exp = expected_path.read_text(encoding="latin1").splitlines()[0]

        if got != exp:
            ok = False
            print(f"[FAIL] {stem}")
            print(f"  identifier: {identifier!r}")
            print(f"  expected  : {exp!r}")
            print(f"  got       : {got!r}")
        else:
            print(f"[OK]   {stem}")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

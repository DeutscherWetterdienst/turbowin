import argparse
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

DEFAULT_VECTORS_DIR = REPO_ROOT / "tests" / "vectors"
SCRIPT = REPO_ROOT / "tests" / "harness" / "run_reference_encoder_windows.py"


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--vector",
        type=Path,
        help="Generate expected output only for a single *.format_101.txt file",
    )
    ap.add_argument(
        "--dir",
        type=Path,
        default=DEFAULT_VECTORS_DIR,
        help="Directory containing *.format_101.txt files (default: tests/vectors)",
    )
    args = ap.parse_args()

    vectors = []
    if args.vector is not None:
        vectors = [args.vector]
    else:
        vectors = sorted(args.dir.glob("*.format_101.txt"))

    for input_path in vectors:
        stem = input_path.stem.replace(".format_101", "")
        if "_id" not in stem:
            print(f"[SKIP] {input_path} (missing _id<STATIONID> in filename)")
            continue

        print(f"[GEN]  {stem}")
        cmd = [
            "python",
            str(SCRIPT),
            str(input_path),
            "--identifier",
            stem.split("_id", 1)[1],
            "--write-expected",
        ]
        subprocess.run(cmd, cwd=REPO_ROOT, check=True)

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

"""Generate expected HPK files with the checked-in Linux reference encoder."""

import argparse
import shutil
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
REFERENCE_BIN = REPO_ROOT / "reference" / "bin" / "teste_hc_TW_64"
PILOTE = REPO_ROOT / "reference" / "config" / "S-AWS-101_modl_pilote.csv"


def generate(vector: Path, identifier: str) -> bytes:
    work = REPO_ROOT / "tests" / ".work" / vector.stem / "format_101"
    if work.exists():
        shutil.rmtree(work)
    (work / "config").mkdir(parents=True)
    (work / "temp").mkdir()
    shutil.copy2(PILOTE, work / "config" / PILOTE.name)
    shutil.copy2(vector, work / "temp" / "format_101.txt")

    process = subprocess.run(
        [
            str(REFERENCE_BIN),
            f"config/{PILOTE.name}",
            "temp/format_101.txt",
            identifier,
            "S-AWS-101",
        ],
        cwd=work,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    output = work / "temp" / "HPK_format_101.txt"
    if not output.exists():
        raise RuntimeError(f"Reference encoder produced no output for {vector}")
    if process.returncode != 0:
        print(f"[WARN] reference encoder exit={process.returncode} but produced {output}")
    return output.read_bytes().splitlines()[0]


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dir", type=Path, required=True)
    args = parser.parse_args()

    for vector in sorted(args.dir.glob("*.format_101.txt")):
        stem = vector.stem.replace(".format_101", "")
        if "_id" not in stem:
            raise ValueError(f"Vector name lacks _id<station>: {vector.name}")
        identifier = stem.split("_id", 1)[1]
        expected = vector.with_name(f"{stem}.expected.hpk.txt")
        expected.write_bytes(generate(vector, identifier) + b"\n")
        print(f"[GEN] {expected}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

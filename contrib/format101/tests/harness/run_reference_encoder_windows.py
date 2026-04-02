import argparse
import shutil
import subprocess
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

BIN = REPO_ROOT / "reference" / "binaries" / "format_101" / "teste_hc_TW.exe"
PILOTE_CSV = (
    REPO_ROOT / "miscellaneous" / "format_101" / "config" / "S-AWS-101_modl_pilote.csv"
)
TEMPLATE = "S-AWS-101"


def run_one(vector_txt: Path, identifier: str, *, keep_workdir: bool = True) -> str:
    name = vector_txt.stem.replace(".format_101", "")
    work = REPO_ROOT / "tests" / ".work" / name / "format_101"
    config_dir = work / "config"
    temp_dir = work / "temp"

    if work.exists():
        shutil.rmtree(work)
    config_dir.mkdir(parents=True)
    temp_dir.mkdir(parents=True)

    shutil.copy2(PILOTE_CSV, config_dir / PILOTE_CSV.name)
    shutil.copy2(vector_txt, temp_dir / "format_101.txt")

    cmd = [
        str(BIN),
        f"config/{PILOTE_CSV.name}",
        "temp/format_101.txt",
        identifier,
        TEMPLATE,
    ]

    proc = subprocess.run(
        cmd,
        cwd=work,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        encoding="latin1",
        check=False,
    )

    out_file = temp_dir / "HPK_format_101.txt"
    if not out_file.exists():
        if keep_workdir:
            (work / "run_stdout.txt").write_text(proc.stdout, encoding="latin1")
        raise RuntimeError(
            f"HPK output not produced. Exit={proc.returncode}. See {work / 'run_stdout.txt'}"
        )

    hpk_line = out_file.read_text(encoding="latin1").splitlines()[0]

    if keep_workdir:
        (work / "run_stdout.txt").write_text(proc.stdout, encoding="latin1")
    else:
        shutil.rmtree(work, ignore_errors=True)

    return hpk_line


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("vector", type=Path, help="Path to *.format_101.txt")
    ap.add_argument(
        "--identifier",
        required=True,
        help="Station ID / indicatif (max 7 chars in TurboWin+)",
    )
    ap.add_argument(
        "--write-expected",
        action="store_true",
        help="Write tests/vectors/<name>.expected.hpk.txt based on the input file name",
    )
    args = ap.parse_args()

    hpk = run_one(args.vector, args.identifier)
    print(hpk)

    if args.write_expected:
        name = args.vector.stem.replace(".format_101", "")
        expected_path = REPO_ROOT / "tests" / "vectors" / f"{name}.expected.hpk.txt"
        expected_path.write_text(hpk + "\n", encoding="latin1")
        print(f"Wrote: {expected_path}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

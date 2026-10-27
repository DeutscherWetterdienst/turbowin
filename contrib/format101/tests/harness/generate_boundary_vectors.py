"""Generate deterministic Format 101 quantization-boundary inputs."""

import argparse
from pathlib import Path

from format101.spec import load_pilote_csv

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_PILOTE = REPO_ROOT / "reference" / "config" / "S-AWS-101_modl_pilote.csv"
DEFAULT_OUTPUT = REPO_ROOT / "tests" / "vectors" / "boundaries"


def render(values: list[tuple[bool, float | None]]) -> str:
    lines = ["0"]
    for present, value in values:
        lines.append(f"1 {value}" if present else "0")
    return "\n".join(lines) + "\n"


def base_values(pilote: list) -> list[tuple[bool, float | None]]:
    values = [(False, None) for _ in pilote]
    for index, value in {
        0: 101.0,
        1: 1.0,
        2: 315.0,
        6: 2026.0,
        7: 4.0,
        8: 2.0,
        9: 15.0,
        10: 0.0,
        11: 12.05,
        12: 45.1,
        28: 0.0,
        39: 0.0,
        48: 0.0,
    }.items():
        values[index] = (True, value)
    return values


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pilote", type=Path, default=DEFAULT_PILOTE)
    parser.add_argument("--out-dir", type=Path, default=DEFAULT_OUTPUT)
    args = parser.parse_args()

    pilote = load_pilote_csv(args.pilote)
    if pilote and pilote[0].bufr == "000000" and not pilote[0].ref:
        pilote = pilote[1:]

    # Indices are after the 000000 operating-mode entry:
    # 2=COG, 3=SOG, 11=latitude, 12=longitude, 13=barometer pressure,
    # 22=air temperature.
    cases = {
        "boundary_sog_below_half_idSOGB01": (3, 0.24),
        "boundary_sog_halfway_idSOGH01": (3, 0.25),
        "boundary_sog_above_half_idSOGA01": (3, 0.26),
        "boundary_sog_below_min_idSOGN01": (3, -1.0),
        "boundary_sog_above_max_idSOGX01": (3, 31.0),
        "boundary_temp_halfway_idTMPH01": (22, 223.20),
        "boundary_lat_halfway_idLATH01": (11, -89.995),
        "boundary_pressure_below_min_idPRSM01": (13, 84995.0),
        "boundary_pressure_above_max_idPRSX01": (13, 105465.0),
        "boundary_lat_min_idLATMIN": (11, -90.0),
        "boundary_lat_max_idLATMAX": (11, 90.0),
        "boundary_lon_min_idLONMIN": (12, -180.0),
        "boundary_lon_max_idLONMAX": (12, 180.0),
        "boundary_temp_min_idTMPMIN": (22, 223.15),
        "boundary_temp_max_idTMPMAX": (22, 325.35),
        "boundary_cog_min_idCOGMIN": (2, 0.0),
        "boundary_cog_max_idCOGMAX": (2, 360.0),
    }
    args.out_dir.mkdir(parents=True, exist_ok=True)
    for stem, (index, value) in cases.items():
        values = base_values(pilote)
        values[index] = (True, value)
        (args.out_dir / f"{stem}.format_101.txt").write_text(
            render(values), encoding="utf-8"
        )
        print(f"[GEN] {stem}: field_index={index} value={value}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

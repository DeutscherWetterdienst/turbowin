from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

BASE_VECTOR = REPO_ROOT / "tests" / "vectors" / "minimal_id34567.format_101.txt"
OUT_DIR = REPO_ROOT / "tests" / "vectors" / "experiments"

# Legacy pilote layout (after skipping the first operating mode "0" line in the file):
# 0..27   main block up to 022042
# 28      410000 visual marker
# 29..38  visual fields (10)
# 39      first 408000 (chain marker)
# 40..47  wave fields (8)
# 48      second 408000 (ice marker)
# 49..56  ice fields (8)
VIS_MARKER_DATA_IDX = 28
VIS_FIELDS_START = 29
VIS_FIELDS_END = 39  # exclusive (29..38)
CHAIN_MARKER_DATA_IDX = 39
WAVE_FIELDS_START = 40
WAVE_FIELDS_END = 48  # exclusive (40..47)
ICE_MARKER_DATA_IDX = 48
ICE_FIELDS_START = 49
ICE_FIELDS_END = 57  # exclusive (49..56)

# Specific field indices we will toggle to create a minimal "present" signal
VV_DATA_IDX = 29  # 020001_code (visual)
PWPW_DATA_IDX = 40  # 022012 (wave)
ICE_THICKNESS_DATA_IDX = 49  # 020031 (ice thickness)

COMMENT_PREFIX = "0"
MISSING_LINE = "0"
PRESENT_LINE_FMT = "1 {value}"


def _parse_lines(p: Path) -> list[str]:
    lines = p.read_text(encoding="utf-8").splitlines()
    if not lines or lines[0].strip() != COMMENT_PREFIX:
        raise ValueError(f"Unexpected base vector format: {p}")
    return lines


def _set_data_line(lines: list[str], data_idx: int, new_line: str) -> None:
    # file line number = 2 + data_idx (1-based)
    file_line_idx = 1 + data_idx  # 0-based list index; since lines[0] is operating mode
    if file_line_idx >= len(lines):
        raise IndexError(f"Base vector too short; cannot set data_idx={data_idx}")
    lines[file_line_idx] = new_line


def _set_marker(lines: list[str], data_idx: int, val: float, label: str) -> None:
    _set_data_line(lines, data_idx, f"1 {val}             {label}")


def _set_missing_block(lines: list[str], start: int, end: int) -> None:
    for i in range(start, end):
        _set_data_line(lines, i, f"{MISSING_LINE}")


def _set_present_value(
    lines: list[str], data_idx: int, value: float, label: str
) -> None:
    _set_data_line(lines, data_idx, f"1 {value}            {label}")


def write_case(case_name: str, config: dict) -> Path:
    lines = _parse_lines(BASE_VECTOR)

    # Start from a fully missing visual/wave/ice configuration
    _set_missing_block(lines, VIS_FIELDS_START, VIS_FIELDS_END)
    _set_missing_block(lines, WAVE_FIELDS_START, WAVE_FIELDS_END)
    _set_missing_block(lines, ICE_FIELDS_START, ICE_FIELDS_END)

    # Apply markers
    _set_marker(
        lines,
        VIS_MARKER_DATA_IDX,
        float(config["visual_marker"]),
        "visual marker (410000)",
    )
    _set_marker(
        lines,
        CHAIN_MARKER_DATA_IDX,
        float(config["chain_marker"]),
        "chain marker (first 408000)",
    )
    _set_marker(
        lines,
        ICE_MARKER_DATA_IDX,
        float(config["ice_marker"]),
        "ice marker (second 408000)",
    )

    # Apply minimal present signals (optional)
    if config.get("visual_vv_present"):
        _set_present_value(lines, VV_DATA_IDX, 98.0, "visibility (VV) [code]")

    if config.get("wave_pwpw_present"):
        _set_present_value(lines, PWPW_DATA_IDX, 10.0, "period of wind waves [seconds]")

    if config.get("ice_thickness_present"):
        _set_present_value(
            lines, ICE_THICKNESS_DATA_IDX, 0.07, "ice deposit/thickness [metres]"
        )

    out = OUT_DIR / f"{case_name}.format_101.txt"
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return out


def main() -> int:
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    # All cases set visual marker = 1 to ensure we get past the visual marker and into the chain marker
    # We keep all group fields missing by default, then selectively set one field present.
    cases = [
        (
            "exp_chain_v1_c0_i0_idCHN000",
            dict(
                visual_marker=1.0,
                chain_marker=0.0,
                ice_marker=0.0,
                visual_vv_present=False,
                wave_pwpw_present=False,
                ice_thickness_present=False,
            ),
        ),
        (
            "exp_chain_v1_c1_i0_idCHN010",
            dict(
                visual_marker=1.0,
                chain_marker=1.0,
                ice_marker=0.0,
                visual_vv_present=False,
                wave_pwpw_present=False,
                ice_thickness_present=False,
            ),
        ),
        (
            "exp_chain_v1_c1_i1_idCHN011",
            dict(
                visual_marker=1.0,
                chain_marker=1.0,
                ice_marker=1.0,
                visual_vv_present=False,
                wave_pwpw_present=False,
                ice_thickness_present=False,
            ),
        ),
        (
            "exp_chain_v1_c1_i0_wave1_idCHN110",
            dict(
                visual_marker=1.0,
                chain_marker=1.0,
                ice_marker=0.0,
                visual_vv_present=False,
                wave_pwpw_present=True,
                ice_thickness_present=False,
            ),
        ),
        (
            "exp_chain_v1_c1_i1_wave1_idCHN111",
            dict(
                visual_marker=1.0,
                chain_marker=1.0,
                ice_marker=1.0,
                visual_vv_present=False,
                wave_pwpw_present=True,
                ice_thickness_present=False,
            ),
        ),
        (
            "exp_chain_v1_c1_i1_ice1_idCHN211",
            dict(
                visual_marker=1.0,
                chain_marker=1.0,
                ice_marker=1.0,
                visual_vv_present=False,
                wave_pwpw_present=False,
                ice_thickness_present=True,
            ),
        ),
    ]

    created = []
    for name, cfg in cases:
        created.append(write_case(name, cfg))

    print("Created:")
    for p in created:
        print(" -", p.relative_to(REPO_ROOT))

    print("\nNext steps (Windows):")
    print("1) Generate expected outputs via reference encoder:")
    print(
        "   uv run --project python python tests/harness/generate_expected_hpk_windows.py --vector tests/vectors/experiments/<case>.format_101.txt"
    )
    print("2) Compare octets and locate differences:")
    print(
        "   uv run --project python python tests/harness/analyze_octet_diff.py --dir tests/vectors/experiments --name <case> --station-id <ID>"
    )
    print(
        "   uv run --project python python tests/harness/analyze_payload_diff.py --dir tests/vectors/experiments --name <case> --station-id <ID>"
    )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

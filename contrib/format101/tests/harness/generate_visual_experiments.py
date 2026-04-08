from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

BASE_VECTOR = REPO_ROOT / "tests" / "vectors" / "minimal_id34567.format_101.txt"
OUT_DIR = REPO_ROOT / "tests" / "vectors" / "experiments"

# In the legacy TurboWin pilote after skipping the first "0" operating mode line,
# the indices are:
# 0..27   main block up to 022042
# 28      410000 visual marker
# 29      020001_code (VV)
#
# In the .format_101.txt file, line 1 is operating mode, and the rest map 1:1 to pilote entries.
# So: data line index N corresponds to file line number N+2 (1-based lines).
VIS_MARKER_DATA_IDX = 28
VV_DATA_IDX = 29


def _parse_lines(p: Path) -> list[str]:
    lines = p.read_text(encoding="utf-8").splitlines()
    if not lines or lines[0].strip() != "0":
        raise ValueError(f"Unexpected base vector format: {p}")
    return lines


def _set_data_line(lines: list[str], data_idx: int, new_line: str) -> None:
    # file line number = 2 + data_idx (1-based)
    file_line_idx = 1 + data_idx  # 0-based list index; since lines[0] is operating mode
    if file_line_idx >= len(lines):
        raise IndexError(f"Base vector too short; cannot set data_idx={data_idx}")
    lines[file_line_idx] = new_line


def write_case(
    case_name: str,
    *,
    marker_present: bool,
    marker_value: float,
    vv_present: bool,
    vv_value: float | None,
) -> Path:
    lines = _parse_lines(BASE_VECTOR)

    # Set marker line
    if marker_present:
        _set_data_line(
            lines,
            VIS_MARKER_DATA_IDX,
            f"1 {marker_value}             visual marker (410000)",
        )
    else:
        _set_data_line(
            lines, VIS_MARKER_DATA_IDX, "0                 visual marker (410000)"
        )

    # Set VV line (020001)
    if vv_present:
        assert vv_value is not None
        _set_data_line(
            lines, VV_DATA_IDX, f"1 {vv_value}            visibility (VV) [code]"
        )
    else:
        _set_data_line(lines, VV_DATA_IDX, "0                 visibility (VV) [code]")

    out = OUT_DIR / f"{case_name}.format_101.txt"
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return out


def main() -> int:
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    cases = [
        # marker=0, VV missing
        (
            "exp_visual_m0_vv0_idVISM000",
            dict(
                marker_present=True, marker_value=0.0, vv_present=False, vv_value=None
            ),
        ),
        # marker=0, VV present
        (
            "exp_visual_m0_vv1_idVISM001",
            dict(marker_present=True, marker_value=0.0, vv_present=True, vv_value=98.0),
        ),
        # marker=1, VV missing
        (
            "exp_visual_m1_vv0_idVISM010",
            dict(
                marker_present=True, marker_value=1.0, vv_present=False, vv_value=None
            ),
        ),
        # marker=1, VV present
        (
            "exp_visual_m1_vv1_idVISM011",
            dict(marker_present=True, marker_value=1.0, vv_present=True, vv_value=98.0),
        ),
    ]

    created = []
    for name, cfg in cases:
        created.append(write_case(name, **cfg))

    print("Created:")
    for p in created:
        print(" -", p.relative_to(REPO_ROOT))

    print("\nNext steps:")
    print("1) Generate expected HPK using the reference binary:")
    print(
        "   uv run --project python python tests/harness/generate_expected_hpk_windows.py --vector tests/vectors/experiments/<file>.format_101.txt"
    )
    print("2) Analyze differences:")
    print(
        "   uv run --project python python tests/harness/analyze_octet_diff.py --dir tests/vectors/experiments --name <file_stem> --station-id <ID>"
    )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

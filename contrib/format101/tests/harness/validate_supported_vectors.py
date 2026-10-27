"""Validate the structural contract for supported TurboWin format_101 inputs."""

from __future__ import annotations

import argparse
import math
from pathlib import Path


FIELD_COUNT = 57
VISUAL_MARKER = 28
VISUAL_FIELDS = range(29, 39)
WAVE_MARKER = 39
WAVE_FIELDS = range(40, 48)
ICE_MARKER = 48
ICE_FIELDS = range(49, 57)


def parse(path: Path) -> list[tuple[bool, float | None]]:
    lines = path.read_text(encoding="utf-8").splitlines()
    if not lines or lines[0].strip() != "0":
        raise ValueError("first line must be operating mode 0")

    values: list[tuple[bool, float | None]] = []
    for raw in lines[1:]:
        text = raw.strip()
        if not text:
            continue
        parts = text.split()
        if parts[0] == "0":
            values.append((False, None))
        elif parts[0] == "1" and len(parts) >= 2:
            value = float(parts[1])
            if not math.isfinite(value):
                raise ValueError("present values must be finite")
            values.append((True, value))
        else:
            raise ValueError("each value line must start with 0 or 1 and present lines need a value")

    if len(values) != FIELD_COUNT:
        raise ValueError(f"expected {FIELD_COUNT} value lines, got {len(values)}")
    return values


def marker(values: list[tuple[bool, float | None]], index: int) -> int:
    present, value = values[index]
    if not present or value is None:
        return 0
    if value not in (0.0, 1.0):
        raise ValueError(f"marker at index {index} must be 0 or 1")
    return int(value)


def validate(path: Path) -> None:
    values = parse(path)
    visual = marker(values, VISUAL_MARKER)
    wave = marker(values, WAVE_MARKER)
    ice = marker(values, ICE_MARKER)

    def require_missing(indices: range, label: str) -> None:
        present = [i for i in indices if values[i][0]]
        if present:
            raise ValueError(f"{label} fields present while its marker chain is absent: {present}")

    if not visual:
        require_missing(VISUAL_FIELDS, "visual")
    if not wave:
        require_missing(WAVE_FIELDS, "wave")
    if not ice:
        require_missing(ICE_FIELDS, "ice")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dir", action="append", type=Path, required=True)
    args = parser.parse_args()

    paths = sorted(path for directory in args.dir for path in directory.glob("*.format_101.txt"))
    if not paths:
        print("[FAIL] No format_101 vectors found")
        return 1

    failed = 0
    for path in paths:
        try:
            validate(path)
        except ValueError as exc:
            failed += 1
            print(f"[FAIL] {path}: {exc}")
        else:
            print(f"[OK]   {path}")

    print(f"Summary: passed={len(paths) - failed} failed={failed}")
    return 0 if failed == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())

# Test harness scripts

This directory contains small, self-contained scripts used to:

- run the reference binaries in `reference/bin/`
- generate golden vectors in `tests/vectors/`
- compare the outputs of different encoder/decoder implementations

Scripts in this directory are expected to be runnable from the repository root.

## Debugging Python encoder mismatches (recommended workflow)

When the Python encoder does not match the reference output for a given vector:

1) Confirm the reference vectors are up to date (Windows only):

```powershell
uv run --project python python tests/harness/generate_expected_hpk_windows.py --vector tests/vectors/generated/<name>.format_101.txt

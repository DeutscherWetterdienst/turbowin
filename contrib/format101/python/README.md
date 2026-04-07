# format101-python

This directory contains the Python package used for the reimplementation of
E‑SURFMAR half-compressed messages, with the initial focus on dataformat #101
(`S-AWS-101`) as used by TurboWin+.

At the moment, the repository mainly provides:

- reference binaries (for local compatibility testing only)
- golden vectors in `tests/vectors/`
- harness scripts in `tests/harness/`
- a Python package scaffold in `python/src/format101/`

The actual encoder/decoder implementation will be added incrementally.

---

## Prerequisites

- Python >= 3.11
- [uv](https://github.com/astral-sh/uv)

---

## Setup (Windows)

From `contrib/format101`:

```powershell
uv sync --project python
```

This creates a virtual environment in `python/.venv` and installs dependencies
(as defined in `python/pyproject.toml`).

---

## Running the test harness (Windows)

The harness scripts live in `tests/harness/` and must be executed from `contrib/format101`
so that relative paths (to `tests/`, `reference/`, `miscellaneous/`) work correctly.

### Verify all golden vectors (reference binary)

```powershell
uv run --project python python tests/harness/verify_reference_vectors_windows.py
```

### Verify all golden vectors (Python encoder)

```powershell
uv run --project python python tests/harness/verify_python_encoder_against_vectors.py
```

### Run reference encoder for a single vector

```powershell
uv run --project python python tests/harness/run_reference_encoder_windows.py tests/vectors/minimal_id34567.format_101.txt --identifier 34567
```

### (Optional) Regenerate an expected output file

```powershell
uv run --project python python tests/harness/run_reference_encoder_windows.py tests/vectors/minimal_id34567.format_101.txt --identifier 34567 --write-expected
```

### Check decoder against golden vectors (tolerance-based)

```powershell
uv run --project python python tests/harness/verify_decoder_against_vectors.py
```

### For debugging the decoder

```powershell
uv run --project python python tests/harness/debug_decode_one.py tests/vectors/minimal_id34567.expected.hpk.txt
```

---

## Running the CLI

From `contrib/format101`:

### Decode

```powershell
uv run --project python format101 decode --hpk-file tests/vectors/minimal_id34567.expected.hpk.txt
```

### Encode

```powershell
uv run --project python format101 encode --format101 tests/vectors/minimal_id34567.format_101.txt --station-id 34567
```

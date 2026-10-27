# Half-compressed / Format 101 tooling (TurboWin+)

This repository contains reference material and work-in-progress implementations
to encode/decode E‑SURFMAR half-compressed messages, in particular dataformat #101
(`S-AWS-101`), as used by TurboWin+.

## Scope
- Primary target: encode/decode `S-AWS-101` (format #101) messages
- Goal: replace opaque native helper binaries in TurboWin+ with reproducible code

## Repository layout
- `reference/bin/`: reference executables
- `reference/config/`: reference config
- `python/`: Python package
- `java/`: Java encoder implementation
- `tests/`: test vectors and harness scripts

The root TurboWin Gradle build compiles the Java encoder as the `format101`
library package, excluding its standalone CLI entry point. The pilot table is
bundled as the classpath resource
`/format101/config/S-AWS-101_modl_pilote.csv`. The library can therefore be
called directly with `format101.Encoder`; it does not start a native helper or
require the CLI. The application boundary still uses the legacy helper until a
later checklist step.

## Compatibility status (TurboWin+ legacy)
The Python encoder/decoder in `python/src/format101/` targets 1:1 compatibility with the
TurboWin+ legacy workflow by default and matches the output of `teste_hc_TW.exe` on the
current golden vectors in `tests/vectors/`.

## Running test campaigns

The reference expected outputs (`*.expected.hpk.txt`) are generated using the Windows reference binary
`reference/bin/teste_hc_TW.exe`. Once generated, Python and Java verification can run on Linux or
Windows without executing the reference binary.

The GitHub Actions compatibility workflow runs the Python and Java verifiers on both Linux and
Windows. Windows additionally checks the checked-in expected outputs against the native reference
binary. These jobs never regenerate expected outputs; generation remains an explicit local Windows
operation.

### Generate and verify combo campaign vectors

```powershell
uv run --project python python tests/harness/generate_vectors.py --mode combo --per-combo 10 --seed 1234 --out-dir tests/vectors/generated_combo --prefix combo
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors/generated_combo
uv run --project python python tests/harness/verify_python_encoder_against_vectors.py --dir tests/vectors/generated_combo
uv run --project python python tests/harness/verify_java_encoder_against_vectors.py   --dir tests/vectors/generated_combo
```

### Generate and verify fuzz campaign vectors

```powershell
uv run --project python python tests/harness/generate_vectors.py --mode random --count 1000 --seed 1234 --out-dir tests/vectors/fuzz_1234 --prefix fuzz
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors/fuzz_1234
uv run --project python python tests/harness/verify_python_encoder_against_vectors.py --dir tests/vectors/fuzz_1234
uv run --project python python tests/harness/verify_java_encoder_against_vectors.py   --dir tests/vectors/fuzz_1234
```

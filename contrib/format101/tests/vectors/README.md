# Test vectors

This directory contains canonical input vectors and expected encoded message outputs.

## File types

### `*.format_101.txt`
Input file format used by the reference encoder binary (`teste_hc_TW*`), as produced by TurboWin+.

### `*.expected.hpk.txt`
Expected encoded output (single line), matching the contents of `HPK_format_101.txt`.

The files are stored as Latin-1 (byte-oriented) because the payload contains bytes like `0x7F`.

## Naming convention

Input vectors follow this naming convention:

- `<name>_id<STATIONID>.format_101.txt`

The station ID (`STATIONID`) is extracted from the filename and passed to the reference encoder as
the `indicatif`/identifier argument. TurboWin+ accepts up to 7 characters for the station ID.

The corresponding expected output file is:

- `<name>_id<STATIONID>.expected.hpk.txt`

## Generating expected outputs (Windows)

To generate or refresh `*.expected.hpk.txt` files via the reference encoder binary:

```powershell
uv run --project python python tests/harness/generate_expected_hpk_windows.py
```

To generate only for a single vector:

```powershell
uv run --project python python tests/harness/generate_expected_hpk_windows.py --vector tests/vectors/<name>_id<STATIONID>.format_101.txt
```

To generate expected outputs for vectors in a different directory:

```powershell
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors/generated
```

## Generating new vectors (spec-driven)

To generate valid TurboWin+ legacy vectors without using the TurboWin UI, use the spec-driven generator.
It produces `*.format_101.txt` vectors that follow basic consistency constraints for group marker bits
(e.g. marker=0 => fields in that group are missing).

### Generate one vector per marker combination (8 total)

```powershell
uv run --project python python tests/harness/generate_vectors.py --mode combo --per-combo 1 --out-dir tests/vectors/generated
```

### Generate random vectors (fuzzing)

```powershell
uv run --project python python tests/harness/generate_vectors.py --mode random --count 200 --seed 1234 --out-dir tests/vectors/generated
```

After generating vectors, create the corresponding expected outputs:

```powershell
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors/generated
```

And then verify compatibility:

```powershell
uv run --project python python tests/harness/verify_reference_vectors_windows.py --dir tests/vectors/generated
uv run --project python python tests/harness/verify_python_encoder_against_vectors.py --dir tests/vectors/generated
```

## Test campaigns (Windows)

The following commands are used to generate larger campaigns of vectors and verify both Python and Java
encoders against the reference binary.

### Combo campaign (marker combinations)

```powershell
uv run --project python python tests/harness/generate_vectors.py --mode combo --per-combo 10 --seed 1234 --out-dir tests/vectors/generated_combo --prefix combo
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors/generated_combo
uv run --project python python tests/harness/verify_python_encoder_against_vectors.py --dir tests/vectors/generated_combo
uv run --project python python tests/harness/verify_java_encoder_against_vectors.py   --dir tests/vectors/generated_combo
```

Tip: you can also run the reference check first:

```powershell
uv run --project python python tests/harness/verify_reference_vectors_windows.py --dir tests/vectors/generated_combo
```

### Fuzz campaign (random)

```powershell
uv run --project python python tests/harness/generate_vectors.py --mode random --count 1000 --seed 1234 --out-dir tests/vectors/fuzz_1234 --prefix fuzz
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors/fuzz_1234
uv run --project python python tests/harness/verify_python_encoder_against_vectors.py --dir tests/vectors/fuzz_1234
uv run --project python python tests/harness/verify_java_encoder_against_vectors.py   --dir tests/vectors/fuzz_1234
```

The Java verification script uses the Gradle `installDist` launcher if available. To force a rebuild:

```powershell
uv run --project python python tests/harness/verify_java_encoder_against_vectors.py --build --dir tests/vectors/generated_combo
```

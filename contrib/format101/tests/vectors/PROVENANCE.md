# Compatibility corpus provenance

This directory is the checked-in compatibility corpus used by the Python and Java
encoder verifiers. The expected HPK files are reference outputs; normal verification
does not regenerate them.

## Corpus

| Input vector | Expected output | Status |
| --- | --- | --- |
| `minimal_id34567` | `minimal_id34567.expected.hpk.txt` | included |
| `minimal_idABCDEFG` | `minimal_idABCDEFG.expected.hpk.txt` | included |
| `presence0_idHIJKLMN` | `presence0_idHIJKLMN.expected.hpk.txt` | included |
| `presence_bits0_idNOPQRST` | `presence_bits0_idNOPQRST.expected.hpk.txt` | excluded; see [`exclusions.txt`](exclusions.txt) |

The three included vectors pass exact byte comparison in both encoder verifiers. The
excluded vector retains both its input and native output for investigation.

## Reference assets

The native assets are stored under `../../reference/` and are identified by SHA-256:

| Asset | SHA-256 |
| --- | --- |
| `bin/teste_hc_TW.exe` | `496d4d79e8c090f18c06f5aa3a02d5a9bd7ffb5f87ad5324eaa3707c4275bb7b` |
| `bin/teste_hc_TW_64` | `9170acbc583a234372b2d8a17f6948e8230a6ae58881d7900928f245d055ee79` |
| `bin/MAWSbin_TW.exe` | `b5eab1e19f67077d0a24805858046a3a04425eae601b4f6fb47f0859e2b2e90c` |
| `bin/MAWSbin_TW_64` | `daf4ba22a83b7f2a1532d73e20a5d2714c96c1bd9d3a585ededda93a319f6fb8` |
| `config/S-AWS-101_modl_pilote.csv` | `a63d28ece6e5b8f910bd87f51001f0ab4d0fd62395210763cdc92c06dd1dce75` |
| `config/S-AWS-101_modl_meta.csv` | `689e918d3f7699b555fa7f555164e33daa2fad9a4bed6e34a81aab367f8ff5b5` |
| `config/DataType.txt` | `8bacdec4bfe4e23ebf827d83d602d7fd84ed043012f41f6624fe21747b67625e` |

The binary hashes also appear in [`reference/bin/SHA256SUMS`](../../reference/bin/SHA256SUMS).
Verify the assets with:

```bash
sha256sum ../../reference/bin/* ../../reference/config/*
```

## Generation and verification

Expected outputs were generated on Windows with the checked-in `teste_hc_TW.exe`
reference encoder. Generation is deliberately separate from verification:

```powershell
uv run --project python python tests/harness/generate_expected_hpk_windows.py --dir tests/vectors
```

The deterministic generator source is `tests/harness/generate_vectors.py`. For a
generated campaign, record its mode, count, seed, output directory, reference asset
hashes, and the generator source revision alongside the campaign. The current
checked-in corpus predates the generator campaign and has no generator seed.

Run the checked-in corpus without changing expected files:

```bash
PYTHONPATH=python/src python3 tests/harness/verify_python_encoder_against_vectors.py
python3 tests/harness/verify_java_encoder_against_vectors.py --build
```

On Windows, the same verifier commands use `python` and the Windows Gradle wrapper;
the CI workflow also runs the native reference verification.

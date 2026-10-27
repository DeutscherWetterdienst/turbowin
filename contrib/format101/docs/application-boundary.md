# Application Format 101 boundary

This is the characterization record for the application boundary in
`src/main/java/turbowin/FORMAT_101.java`. Preparation and consumption remain
unchanged; compression now uses the in-process Java encoder. The former native
compressor remains private rollback and characterization code only.

## Control flow

`compress_and_decompress_101_control_center()` performs the following sequence:

1. Selects the legacy module executable name for compatibility provisioning.
2. Checks for the legacy module below `main.logs_dir/format_101/` and, when it is
   absent, attempts to create/copy the module. Decoder provisioning is scheduled
   for removal; the workflow no longer executes a native helper.
3. Selects the compression identifier: `main.station_ID` when it is non-null
   and its untrimmed length is greater than two; otherwise the literal
   `unknown`. Masked and normal call signs are not fallback sources in the
   current code.
4. Requires `format_101/temp/` to exist and deletes its direct entries before
   preparing a new observation.
5. Writes the prepared input and invokes the Java encoder in process.
6. Leaves the generated HPK file for the reporting consumer.

The method uses a `doorgaan` gate for setup and temp-directory cleanup. No native
decoder is invoked. Java compression failures are logged, and
the existing workflow remains responsible for handling missing or empty HPK
output.

## Files and process contracts

All paths are below `main.logs_dir/format_101/`:

| Purpose | Path or command detail |
| --- | --- |
| Pilot/config input | `config/S-AWS-101_modl_pilote.csv` |
| Prepared input | `temp/format_101.txt` |
| Compressed output | `temp/HPK_format_101.txt` (the Java encoder writes Latin-1 text and derives the `HPK_` prefix) |
| Compression log | Java compression status is printed; the legacy native log is retained only for the private rollback path |
| Identification record | No longer created; it was only used by the diagnostic decoder |
| Decompression log | No longer created by the application |
| Decompressed diagnostic output | No longer created by the application |

The former native compressor ran with the `format_101/` directory as its
working directory and received:

```text
<compression executable> config/S-AWS-101_modl_pilote.csv temp/format_101.txt <identifier> S-AWS-101
```

The former diagnostic identification record contained the configured IMO number,
selected identifier, encryption flag `1`, ship name, `INMC_SC`, and template
`S-AWS-101`, separated by semicolons. It is no longer part of the application
contract.

## Prepared input and failure behavior

The preparation method writes operating mode `0` followed by exactly 57
TurboWin-format value lines. Present values include a numeric value and a
description; absent values contain only `0` and the description. It catches an
input-file `IOException`, prints a warning, and does not return a status to the
caller.

Before preparation, temp cleanup removes stale direct files and directories.
If the temp directory is missing, cleanup returns failure and the control
center skips compression and decompression. This prevents an old compressed
output from being silently reused, but the native compressor's own missing or
empty output is not independently validated by the legacy boundary.

These observations are covered by
`src/test/java/turbowin/Format101CharacterizationTest.java`, including path and
argument assertions, representative prepared input, identification-file
contents, stale-entry removal, and missing-temp failure.

The Java encoder reads the pilot table and prepared input directly, writes the
encoded observation to `HPK_format_101.txt` using ISO-8859-1, and does not start
a compressor subprocess. Native decoding is no longer part of the application
workflow. The native decoder and its configuration remain only until the
separate production-resource cleanup step is completed; reference copies remain
available under `contrib/format101/reference/`.

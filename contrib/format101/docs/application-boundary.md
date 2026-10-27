# Existing application Format 101 boundary

This is the characterization record for the legacy boundary in
`src/main/java/turbowin/FORMAT_101.java`. It describes behavior that a new
encoder must preserve or deliberately change in a separately reviewed step.

## Control flow

`compress_and_decompress_101_control_center()` performs the following sequence:

1. Selects the compression and decompression executable names from the detected
   operating system.
2. Checks for the compression executable below `main.logs_dir/format_101/` and,
   when it is absent, attempts to create/copy the complete legacy module.
3. Selects the compression identifier: `main.station_ID` when it is non-null
   and its untrimmed length is greater than two; otherwise the literal
   `unknown`. Masked and normal call signs are not fallback sources in the
   current code.
4. Requires `format_101/temp/` to exist and deletes its direct entries before
   preparing a new observation.
5. Writes the prepared input and invokes the native compressor.
6. Writes `config/HC_ident.txt`, then invokes the native decompressor.

The method uses a `doorgaan` gate for setup, temp-directory cleanup, and
identification-file failure. The compressor and decompressor process exit codes
are printed and logged through their redirected output, but a nonzero compressor
exit does not itself stop identification-file creation or decompression.

## Files and process contracts

All paths are below `main.logs_dir/format_101/`:

| Purpose | Path or command detail |
| --- | --- |
| Pilot/config input | `config/S-AWS-101_modl_pilote.csv` |
| Prepared input | `temp/format_101.txt` |
| Native compressed output | `temp/HPK_format_101.txt` (the native compressor derives the `HPK_` prefix) |
| Compression log | `log/log_compression.txt` |
| Identification record | `config/HC_ident.txt` |
| Decompression log | `log/log_decompression.txt` |
| Decompressed diagnostic output | `temp/do_not_send_decompressed_FM13_obs.txt` |

The compressor runs with the `format_101/` directory as its working directory
and receives:

```text
<compression executable> config/S-AWS-101_modl_pilote.csv temp/format_101.txt <identifier> S-AWS-101
```

The identification record is one line containing the configured IMO number,
selected identifier, encryption flag `1`, ship name, `INMC_SC`, and template
`S-AWS-101`, separated by semicolons. The decompressor receives the IMO number,
`temp/HPK_format_101.txt`, `HC_ident.txt`, `config`, mandatory `-h`, logging
`-l`, and the diagnostic output path.

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

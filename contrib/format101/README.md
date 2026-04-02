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

## Compatibility status (TurboWin+ legacy)
The Python encoder/decoder in `python/src/format101/` targets 1:1 compatibility with the
TurboWin+ legacy workflow by default and matches the output of `teste_hc_TW.exe` on the
current golden vectors in `tests/vectors/`.

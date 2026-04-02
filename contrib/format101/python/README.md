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

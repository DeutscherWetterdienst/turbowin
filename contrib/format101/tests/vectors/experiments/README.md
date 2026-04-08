# Experiments

This directory contains experiment vectors used to reverse engineer the exact behavior
of the reference encoder (`teste_hc_TW.exe`).

## Invalid / undefined behavior vectors

Some input combinations trigger output that is not plausible for Format 101 transmission
(e.g. excessively large payloads). These cases are treated as invalid/undefined behavior
and are excluded from "must match reference" tests.

Excluded experiment stems:

- `exp_visual_m0_vv1_idVISM001`

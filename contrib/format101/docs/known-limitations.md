# Known limitations / special cases (TurboWin+ legacy compatibility)

This repository aims for 1:1 compatibility with the TurboWin+ legacy `format_101`
workflow and the reference binaries in `reference/bin/`.

During compatibility testing, a few special cases were identified. These are not
arbitrary test exceptions, but follow from the legacy format design and/or the
reference encoder/decoder behavior.

---

## 1) `001199` (Callsign encryption indicator) ambiguity

In the legacy TurboWin vectors, `001199` is marked as present in `format_101.txt`
(e.g. `1 1.0`). However, when decoding the resulting HPK line via the observed
bitstream/MISSING convention, the field may appear as absent.

Reason:

- The repository (and the reference ecosystem code) uses the convention:
  **MISSING = all bits set to 1** for a field.
- For a 1-bit field, `all bits set` is `1`, which collides with the valid value range
  of the callsign encryption indicator (0/1).

Therefore, `001199` cannot be reliably distinguished between:
- value `1`, and
- `MISSING`

when strictly applying "all-ones means missing" per-field.

For 1:1 compatibility, tests treat `001199` as a known special case.

---

## 2) Group marker bits (`410000` / `408000`) are control fields

The legacy pilot CSV contains short delayed replication factor markers:

- `410000`: visual group marker (replication / presence indicator)
- `408000`: wave group marker (replication / presence indicator)
- `408000`: ice group marker (replication / presence indicator, second occurrence)

These are not regular observation values, but control whether a subsequent block is
present in the bitstream (variable-length message).

Additionally, `408000` occurs twice and cannot be uniquely referenced by BUFR ID alone.

For testing and internal representation, the decoder emits distinct keys:

- `410000_visual`
- `408000_wave`
- `408000_ice`

This avoids key collisions and allows explicit checks of the control bits.

---

## 3) Invalid / undefined behavior inputs

Some inputs can trigger output from the reference encoder that is not plausible for
Format 101 transmission (e.g. excessively large payloads / decoded octet streams).

These cases are treated as invalid/undefined behavior and are excluded from strict
"must match reference" tests in this repository.

Example:
- `tests/vectors/experiments/exp_visual_m0_vv1_idVISM001`

---

## Notes

- These limitations apply to the **legacy compatibility target** and the observed
  behavior of the reference binaries.
- A future "clean" reimplementation may choose to model certain edge cases
  differently, but that would be a deliberate compatibility break.

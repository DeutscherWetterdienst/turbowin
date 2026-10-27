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

## 3) Reference encoder may output messages rejected by Processing Centre validation

Some inconsistent inputs (e.g. marker bits indicating blocks are absent while many
fields within those blocks are still marked present) can cause the reference encoder
(`teste_hc_TW*`) to output extremely long half-compressed messages.

On the Processing Centre side, such messages would be rejected by a basic validator
(e.g. length and allowed character range constraints). The repository's encoder-
compatibility tests exclude only vectors listed with a reason in
`tests/vectors/exclusions.txt`. An invalid reference output without an explicit
manifest entry is a test failure. The input and expected output remain available
for investigation.

## 4) Supported input contract and inconsistent group fields

The supported `format_101.txt` contract is deliberately narrower than “any
numeric file accepted by the native executable”:

- the file begins with operating mode `0` and contains exactly the 57 value
  lines defined after the `000000` pilot entry;
- each value line is either `0` (missing) or `1 <finite numeric value>`;
- the `410000` visual marker and the two `408000` chain markers are 0/1
  control values; and
- fields belonging to a group whose own marker is 0 are missing. The markers
  also control which later blocks are emitted, so fields after a stopping
  marker are ignored by the encoder; they are nevertheless checked against
  their own marker for input consistency.

`tests/harness/validate_supported_vectors.py` checks this structural contract
for the checked-in supported campaigns. It does not impose additional numeric
range restrictions here; quantization and clamping remain part of the pilot
table compatibility behavior.

`presence_bits0_idNOPQRST` intentionally violates the group-field rule: it
sets fields in all three groups present while all three markers are zero. The native
reference emits a 781-byte result for it, whereas Java and Python follow the
control markers and emit a normal short message. This input is therefore
outside the supported contract, remains in the corpus for investigation, and
is excluded only through the explicit manifest entry in
`tests/vectors/exclusions.txt`.

---

## Notes

- These limitations apply to the **legacy compatibility target** and the observed
  behavior of the reference binaries.
- A future "clean" reimplementation may choose to model certain edge cases
  differently, but that would be a deliberate compatibility break.

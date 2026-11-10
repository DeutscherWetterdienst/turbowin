# ADR-0003: Reconstruct Format 101 from compatibility evidence

- Status: Accepted
- Date: 2026-04-02

## Context

The legacy Format 101 behavior was not fully captured by a single readable specification. The
reference tools exposed details such as optional-group marker chains, presence bits, 8-to-6
compaction, bit ordering, padding, quantization, and canonical text tails. These details are
interoperability behavior, not incidental implementation choices.

## Decision

Reconstruct the codec incrementally from native reference binaries, supplied pilote data, golden
vectors, generated vectors, and field/bitstream comparisons. Treat the reference outputs and
Processing Centre validation as compatibility evidence, while documenting undefined or rejected
inputs instead of silently treating them as valid. Keep the reconstruction harness and native
references in `contrib/format101`; the resulting production implementation is governed by
[ADR-0006](0006-java-format-101.md).

## Consequences

Format 101 changes require vector-based verification at field, bitstream, octet, and downstream
consumer boundaries. Some reference outputs must remain explicit exclusions when the Processing
Centre rejects them. The compatibility tooling has its own dependencies and campaigns and is not a
replacement for the application’s normal test task.

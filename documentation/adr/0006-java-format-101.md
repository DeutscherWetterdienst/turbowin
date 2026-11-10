# ADR-0007: Use Java Format 101 in production

- Status: Accepted
- Date: 2026-10-27

## Context

Format 101 is an interoperability boundary with legacy S-AWS-101/HPK data, marker-controlled
optional groups, 6-bit compaction, and reference encoders whose output can expose ambiguous tails.
The application previously depended on native helpers, while compatibility work needed to continue
using those helpers as references.

## Decision

Use the in-process Java Format 101 encoder for production output. Keep native executables and
reference corpora only in `contrib/format101` for deliberate compatibility comparison and reverse
engineering; do not package or invoke them from the production application. Verify the Java encoder
with golden vectors, boundary/domain cases, round trips, and downstream reporting consumers. Keep
documented exclusions for reference outputs rejected by Processing Centre validation.

## Consequences

Production packaging is simpler and avoids a native-runtime dependency, but the Java implementation
must preserve legacy bit layouts, marker chains, padding, quantization, missing values, and text-tail
canonicalization. Compatibility campaigns remain a separate toolchain and are not implied by the
root Gradle test task.

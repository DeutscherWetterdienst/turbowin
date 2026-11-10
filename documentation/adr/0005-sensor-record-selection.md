# ADR-0005: Preserve sensor-record selection policies

- Status: Accepted
- Date: 2026-10-23

## Context

Sensor history files contain records from different devices and workflows. “Latest” can mean the
last physical line, the last matching line, the latest valid record, or a legacy file-order result
where an invalid duplicate can invalidate an earlier valid record. Replacing these meanings with
one generic reader would silently change observations and reports.

## Decision

Keep reader methods and call sites explicit about their selection policy. Reuse parsing and file
mechanics only where the caller's policy is unchanged. Preserve the established timestamp, record
length, checksum, freshness, and malformed-input behavior, including intentionally silent rejection
and duplicate-record invalidation where characterized.

## Consequences

Reader names and tests must communicate policy rather than merely format. A shared helper is not
automatically safe to substitute for another reader. New device workflows require tests for record
order, duplicates, malformed records, age boundaries, and the distinction between validity and
freshness.

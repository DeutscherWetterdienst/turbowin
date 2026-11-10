# ADR-0006: Migrate legacy user data during startup

- Status: Accepted
- Date: 2026-10-28

## Context

Older installations stored `configuration.txt`, `logs`, and `amver` below the historical
`user.dir` location. The application now uses the system-wide data root defined by
`StartupEnvironment`. Leaving existing data behind would make an upgrade appear to lose settings
and history.

## Decision

On startup, when no `turbowin.data.dir` override is active, inspect the historical location and
merge only the legacy `configuration.txt`, `logs`, and `amver` entries into the current data root.
Do not overwrite destination entries that already exist. Retain the source data for inspection or
rollback; launcher marker files and application resources are not migration inputs.

## Consequences

Migration is best-effort and is not an atomic transaction. A destination that already contains a
file wins, so operators may need to resolve conflicting copies manually. Startup tests must use
isolated temporary roots and verify both migration and non-overwrite behavior.

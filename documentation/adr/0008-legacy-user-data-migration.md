# ADR-0006: Migrate legacy user data during startup

- Status: Accepted
- Date: 2026-10-28

## Context

Older installations stored `configuration.txt`, `logs`, and `amver` below the historical
installation bin directory. The application now uses the system-wide data root defined by
`StartupEnvironment`. Leaving existing data behind would make an upgrade appear to lose settings
and history.

## Decision

On startup, when no `turbowin.data.dir` override is active, inspect known Windows, Debian, and
versioned RPM installation locations. The launch working
directory (`user.dir`) is not a discovery input. Try each installation’s `logs/configuration.txt`
then its bin-root `configuration.txt`, validating before selecting settings. Write the selected
settings only to `<data root>/configuration.txt`. Ordinary saves maintain a previous-valid `.bak`
beside it; startup can recover from that backup.
Merge legacy `logs` and `amver` histories separately, excluding legacy configuration copies.
Do not overwrite destination entries that already exist. An existing current configuration backup
also prevents introducing legacy settings, allowing normal startup recovery to select it.
Retain the source data for inspection or
rollback; launcher marker files and application resources are not migration inputs.

## Consequences

Migration is best-effort and is not an atomic transaction. A destination that already contains a
file wins, so operators may need to resolve conflicting copies manually. Startup tests must use
isolated temporary roots and verify both migration and non-overwrite behavior.

The [configuration paths and migration guide](../configuration_migration.md) records the exact
layouts, selection order, logs-path rebasing, and manual import procedure.

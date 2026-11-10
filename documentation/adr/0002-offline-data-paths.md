# ADR-0002: Use offline desktop mode and shared application data paths

- Status: Accepted
- Date: 2025-02-12

## Context

The web startup route was deprecated, and installed desktop deployments needed a writable,
shared location for configuration, logs, and AMVER data. A process working directory is not a
reliable or permission-safe application data location.

## Decision

Start the installed application in offline desktop mode by default. Resolve writable application
data through platform-specific system locations, with `turbowin.data.dir` available for isolated
tests. Keep packaged resources relative to the `jpackage` application directory (`APPDIR`) and
use a launcher wrapper to establish the shared-access `umask` where applicable.

## Consequences

Installations must provision writable data directories and appropriate permissions. Offline mode
does not mean that selected email, server, map, or other external-resource operations work without
network access. Existing configurations and logs can require migration; that migration is recorded
separately in [ADR-0008](0008-legacy-user-data-migration.md).

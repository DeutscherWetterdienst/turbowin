# Risks and Technical Debt

The refactoring improves code navigation and testability. The following constraints remain;
extraction alone does not establish their removal.

## Shared state and lifecycle

- Static observation/configuration values and Swing component references couple forms,
  coordinators, and extracted workflows. Test order and overlapping background operations can
  affect results if state is not isolated or completion is not awaited.
- Constructor completion, transport return, and worker `done()` are different milestones.
  Modal dialogs, timers, sockets, and device resources can outlive a window. The single-instance
  socket must be released by test fixtures; window disposal alone is insufficient.
- Legacy background code still contains direct UI interaction. Workflow extraction is not proof
  that all Swing operations follow EDT rules.

## Legacy behavior and domain knowledge

- File layouts, code meanings, units, missing values, rounding, and validation order are external
  and domain contracts. An apparently equivalent simplification can lose that information or
  change malformed-input handling.
- Record ordering matters: last physical, latest valid, and historical file-order policies are
  different. Freshness and hourly file-selection boundaries need their own regression coverage.
- Error handling is inconsistent by design/history: silent rejection, diagnostics, defaults,
  clearing values, and dialogs are all present. Some output failure paths log/reset observations,
  so a cleared screen or an IMMT entry must not be interpreted as confirmed delivery.
- Large generated forms, numbered component names, and mixed Dutch/English identifiers remain.
  Smaller helpers reduce the amount of code to inspect without eliminating these constraints.

## Operational and external dependencies

- Ship connectivity can fail or be intermittent. Retries and response handling are transport-
  specific; an HTTP/SMTP result is not confirmation of downstream meteorological processing.
- Platform data directories require writable permissions. Configuration copies are separate
  writes, not an atomic backup transaction; missing configuration can leave existing globals.
- Jakarta SMTP delivery remains dependent on the configured server, credentials, security mode,
  and network connectivity. A successful ARM64 package build does not establish instrument-driver
  compatibility on that target.
- HTTP versus HTTPS and configured email security depend on the selected path/settings. Generated
  email build constants and diagnostic output also require care when handling credentials/data.
- Manual observation quality still depends on observer input and interpretation despite the
  application's quality checks.
- Full verification needs a real or virtual desktop. A headless run can skip UI classes and fail
  other tests that construct dialogs; CI timeout/recording artifacts aid diagnosis but do not
  replace lifecycle isolation.

There is no documented decision to migrate the GUI framework or redesign persistence. Any such
proposal should be evaluated separately from behavior-preserving maintenance. Generated release
artifacts remain outside Git; see the [artifact policy](repository_cleanup.md) and
[testing guide](contributing_and_testing.md).

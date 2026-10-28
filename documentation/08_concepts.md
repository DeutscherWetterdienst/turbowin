# Cross-cutting Concepts

## Forms, state, and delegation

The main window owns observation entry and menu actions; specialized forms own additional input
and settings. Generated `initComponents()` code coexists with handwritten initialization and
update methods. Many legacy entry methods now delegate to named workflows and helpers.

Observation values, configuration, Swing component references, and device/reporting flags still
live in mutable static fields in `main`, `main_RS232_RS422`, and the `my*` form classes. The
extracted classes are not automatically independent of that state. Some parsers/calculators
accept explicit inputs; composers and many validators/workflows still read globals. There is no
single immutable observation snapshot shared by all operations.

`ObservationStateResetter` resets model values; `ObservationScreenResetWorkflow` refreshes the
visible fields. Disposing a window is separate from releasing sockets, timers, workers, or serial
resources. The contributor guide explains the corresponding [test isolation rules](contributing_and_testing.md#test-isolation-and-completion).

## Background work and Swing completion

`SwingWorker.doInBackground()` runs away from the event-dispatch thread (EDT); `done()` runs on
the EDT and commonly performs dispatch, logging, reset, or notification. A transport invocation
or cleared background sentinel is therefore not necessarily the end of a workflow. Custom email
transport calls additionally use a virtual-thread executor inside the background operation.
Completion callbacks can launch further background work, such as the IMMT log writer; completion
of the initiating worker is not proof that all related file writes have finished.

This describes the intended execution split, not universal thread-safety: legacy code can still
access shared Swing references or display dialogs from background work. New changes should respect
Swing's EDT rules without silently changing modal ordering or existing completion behavior.

## Configuration and local persistence

Configuration remains the legacy human-readable `configuration.txt` line format. Responsibility
is split across `ConfigurationFileStore`, line/value parsers, `ConfigurationManager`, and the
read/write workflows; `ConfigurationPersistenceWorkflow` retains the compatibility entry points.

The reader prefers `configuration.txt` in `logs_dir` and falls back to the data root when the log
copy is absent. If neither file exists, it retains the normal non-empty-directory fallback for
first use. Read I/O failures are tolerated and the existing line array is then applied. Writing
starts separate background writes to each non-empty data/log directory. Those writes are not a
single atomic transaction and have different failure notification paths.

The installed desktop uses platform data directories with `logs` and `amver` subdirectories.
`-Dturbowin.data.dir=...` overrides the data directory, notably for isolated tests. See the
[deployment view](07_deployment_view.md) for paths, permissions, and the legacy-data migration.

IMMT observations, system diagnostics, sensor histories, exports, and configuration are different
files with different lifecycles. IMMT logging/reset is triggered by specific output paths, including
some failure paths, rather than by a global "saved and delivered" transaction. File formats and
existing failure behavior are compatibility contracts, not interchangeable storage details.

## Coding and validation

FM13 observation composition, AWS coding, and Format 101 preparation retain domain-specific
missing-value markers, code mappings, units, rounding, and field positions. `UNDEFINED`, empty
strings, slash placeholders, and numeric invalid sentinels do not have one universal meaning.
For example, Format 101 ship-speed conversion takes legacy knot values and writes metres per
second to three decimal places; coordinate padding and hemisphere conventions also matter.

Level-two validation coordinates consistency checks; level-three validation coordinates unusual
value/combination confirmations. Position/time sequencing and land/sea-mask checks have their
own workflows. Validation order and the `doorgaan` (continue) flag determine which check or dialog
is reached next. Changing order, accepting whitespace, or normalizing an unknown code can change
observable behavior even if the code becomes shorter.

## Sensor record contracts

Record readers deliberately expose different selection policies:

| Reader/policy | Meaning |
| --- | --- |
| `SensorDataRecordReader.readLatestRecord` | Last physical line, not necessarily valid or fresh. |
| `SensorDataRecordReader.readLatestMatchingRecord` | Last line satisfying the caller's predicate. |
| `MintakaHistoricalRecordReader.readLatestValidRecord` | Latest valid matching timestamp; malformed matches are skipped silently. |
| `MintakaHistoricalRecordReader.readLatestMatchingRecord` | Legacy file-order history policy: invalid duplicates or short records can invalidate an earlier position. Used by GPS, wind, and WOW/APR history. |

Do not substitute these policies for one another. Timestamp suffixes, Star versus StarX comma
counts, checksums, coordinate substrings, hourly filenames, and clock/freshness boundaries are
part of the sensor contract. Validation is not equivalent to freshness; workflow-specific age
limits still apply after parsing.

Malformed-input diagnostics also have contracts. For example, a trailing third comma in a Duo
record does not add a generic format diagnostic. An empty StarX dashboard age is rejected without
a number-format diagnostic, while non-empty malformed ages (including whitespace) still produce
one. Rejection, logging, and clearing/retaining existing readings must be considered separately.

## Communication, themes, and external resources

Output paths include files/clipboard, desktop email, Jakarta SMTP, server HTTP/HTTPS, and
configured automatic reports. Transport result handling, retries, and cleanup
belong to the specific path; there is no uniform delivery guarantee.

### Legacy email configuration

Existing configuration files may still contain the historical `python_email` module value.
TurboWin normalizes that value to `jakarta_email` when loading the configuration, preserving the
SMTP host, port, security, credentials, and recipient settings. The normalized value is written
the next time the configuration is saved. The retired Python helper and its architecture-specific
binaries are no longer supported or shipped.

`theme_mode` and theme workflows support the different lighting conditions aboard ship. Theme
restart, system tray support, browsers/maps, and packaged help/resources remain tied to desktop
and platform behavior. Format 101 encoding runs through the in-process Java library; the separate
[compatibility tooling](../contrib/format101/README.md) retains native reference binaries only for
regression comparison and investigation.

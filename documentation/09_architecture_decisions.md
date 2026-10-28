# Architecture Decisions

These describe choices visible in the current implementation and their tradeoffs. They are not a
record of previously undocumented historical motives or commitments to future redesigns.

## Java 21, Swing, and packaged runtimes

The application uses Java 21 and Swing. Java provides a shared implementation across deployment
platforms, while `jlink`/`jpackage` supply an application runtime in the installers. Serial hardware
and desktop APIs still impose platform-specific constraints.

## Preserve behavior while extracting responsibilities

Legacy UI/device entry points delegate to named workflows, parsers, formatters, and calculators.
The extraction makes responsibilities easier to locate and allows explicit-input helpers to be
tested independently. It does not replace the application's shared static state or make every
workflow a separately isolated module.

Refactoring should preserve observable behavior and the information needed to understand it:
domain terminology, units, code meanings, field layouts, thresholds, validation order, diagnostics,
and unusual failure handling. A behavior change requires an explicit decision and regression
coverage, rather than being hidden inside an extraction. See the
[contributor guide](contributing_and_testing.md#preserving-legacy-contracts).

## Device-specific parsing and shared mechanics

Vaisala, Mintaka, EUCAWS, OMC-140 AWS, and GPS integrations retain format-specific parsing and
workflow decisions. Shared file-reading, timestamps, and validation helpers remove duplicated
mechanics without imposing one validity, freshness, or record-selection policy on every device.

## Files and legacy formats remain the interoperability boundary

Configuration text, IMMT logs, FM13 observations, and Format 101 data retain their existing external
representations. Format 101 encoding uses the in-process Java library. Native reference binaries
remain in the separate compatibility tooling for regression comparison, not as production
dependencies. Output logging and reset are workflow side effects, not a transactional delivery record.

## Background work with workflow-specific completion

Long-running operations commonly use `SwingWorker`; their completion callbacks update UI/state
and perform finalization. Custom email transport runs through a virtual-thread executor within
that background work. Returned statuses and exceptions have different handlers and side effects.
This structure does not guarantee universal thread-safety or uniform retry/recovery behavior.

## Characterization and regression tests

JUnit characterization tests protect existing coding, parsing, validation, and error behavior;
AssertJ Swing tests exercise UI/startup behavior. Shared-state fixtures and completion waits are
necessary because extracted code still uses global state. A green helper test alone does not
establish that all asynchronous or UI paths preserve behavior.

# ADR-0010: Migrate the user interface incrementally from Swing to JavaFX

- Status: Accepted
- Date: 2026-11-19

## Context

TurboWin is an established Swing desktop application whose forms interact with shared state,
meteorological workflows, device integrations, and persistent data. A wholesale UI replacement
would put existing behavior and platform packaging at risk and require a large change before it
could be validated. The project needs a way to modernize the interface while continuing to operate
and maintain the existing application.

## Decision

Use JavaFX for the gradual migration of the desktop interface. Migrate individual dialogs or views
in small, independently verifiable steps. Swing and JavaFX will coexist in the same application
throughout the migration; existing Swing windows and workflows continue to be supported. The About
dialog is the first migrated view, not a mandate to replace all other windows at once. This
decision does not set a deadline for removing Swing.

Embed JavaFX views in Swing using `JFXPanel` and reuse the integration pattern established for the
About dialog where appropriate. Create and update Swing components on the Swing event dispatch
thread and JavaFX scenes on the JavaFX application thread; dispatch actions across that boundary
explicitly. Keep UI presentation separate from application logic and reuse existing meteorological
logic, data storage, formats, and device integrations. Each migration must preserve the behavior,
information, and functions of the view it replaces and include appropriate verification.

Continue using the existing Gradle, `jlink`, and `jpackage` build and deployment paths. Include
JavaFX modules and platform-specific runtime dependencies in each supported platform build. The
[JavaFX migration guide](../javafx-migration.md) describes the current integration and testing
approach.

## Consequences

The interface can evolve without waiting for a full rewrite, and existing Swing functionality
remains available during the transition. The application must maintain and test two UI toolkits,
their threading and lifecycle boundary, and native JavaFX dependencies alongside existing Swing
behavior. Graphical integration tests need a display, and packaged runtimes must be verified on
their target platforms. The migration does not by itself remove shared state or isolate existing
domain logic from the UI.

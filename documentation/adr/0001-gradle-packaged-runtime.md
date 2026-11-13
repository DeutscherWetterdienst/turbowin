# ADR-0001: Use Gradle and packaged runtimes

- Status: Accepted
- Date: 2024-11-18

## Context

TurboWin is a desktop Java application deployed across Windows and Linux systems. The build,
dependency, test, and installer paths had to become reproducible while retaining platform-specific
serial and desktop integrations.

## Decision

Use Gradle as the build and dependency entry point, Java 25 as the build/runtime baseline, and
`jlink`/`jpackage` to produce installers containing their runtime. CI builds and tests the supported
platform targets, including Linux amd64 and ARM64 packages and Windows x64. Platform scripts and
the wrapper remain the supported entry points; generated packages and release artifacts stay out of
the source tree.

## Consequences

Installers do not require a separately installed Java runtime, but the build depends on a matching
JDK and platform packaging tools. Native drivers, desktop integration, permissions, and package
tooling remain platform-specific. Build-time application metadata and email constants are generated
by Gradle and must be supplied for a real release.

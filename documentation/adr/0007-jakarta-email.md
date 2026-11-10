# ADR-0008: Use Jakarta Mail as the custom email module

- Status: Accepted
- Date: 2026-10-28

## Context

TurboWin+ had a Python email module, architecture-specific helper binaries, and a Java/Jakarta SMTP
path. Maintaining two custom transports duplicated configuration, failure handling, and packaging
concerns.

## Decision

Use Jakarta Mail as the only custom SMTP module. Normalize legacy `python_email` configuration to
`jakarta_email` when reading it, preserve the other SMTP settings, and write the normalized value on
the next configuration save. Remove the retired Python workflow, helper, binaries, and production
documentation after characterization tests cover delivery, authentication, and failure behavior.

## Consequences

Existing configurations remain usable without retaining the Python runtime. Deployments still
depend on SMTP settings, credentials, security mode, and network connectivity. Desktop email remains
a separate handoff path; neither it nor a successful SMTP operation proves downstream acceptance.

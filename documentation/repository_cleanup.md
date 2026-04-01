# Repository cleanup and artifact policy

This document describes the repository cleanup that was performed to reduce repository size and simplify repository hosting and migration.

## Summary

The repository history was rewritten to remove large binary artifacts and discontinue Git LFS usage.

## Removed from history

The following content was removed from Git history:

- `binaries/`
- `.gitattributes`
- `pilot_charts/`
- `miscellaneous/jar/turbowin_jws.jar`
- `SMDoutput_V4.exe`

In addition:

- historical tags were removed

## Why

The repository previously contained large binary artifacts, partly committed directly and partly managed through Git LFS.

This caused multiple problems:

- unnecessarily large repository size
- more difficult migration between Git hosting platforms
- Git LFS transfer and pointer inconsistencies
- avoidable cloning and mirroring overhead

## Current policy

The repository should contain:

- source code
- build and packaging scripts
- documentation
- runtime resources that are still required by the application

The repository should not contain:

- release installers
- generated packaging outputs
- large binary release artifacts
- Git LFS-managed content

Git LFS is no longer used.

## Remaining legacy helper binaries

A small number of helper binaries still remain in the repository because they are currently required for application functionality:

- `src/main/resources/turbowin/format_101/MAWSbin_TW.exe`
- `src/main/resources/turbowin/format_101/MAWSbin_TW_64`
- `src/main/resources/turbowin/format_101/teste_hc_TW.exe`
- `src/main/resources/turbowin/format_101/teste_hc_TW_64`
- `src/main/resources/turbowin/python_binaries/email_tbw_43`
- `src/main/resources/turbowin/python_binaries/email_tbw_43.exe`

These are technical debt and should be replaced, externalized, or reworked in the future if possible.

## Pilot charts

`pilot_charts/` was removed from the repository history.

The application currently references pilot chart PDFs via external URLs.
Those URLs should point to a maintained external hosting location rather than repository raw-file URLs.

## Notes for maintainers

Because repository history was rewritten:

- commit hashes changed
- old clones should not be reused without care
- migration or replacement of existing remotes may require force-pushes
- old external links to repository-hosted binary artifacts may no longer be valid

## Guidance for future contributions

Please do not commit:

- installers
- release packages
- generated archives
- large binary artifacts

Prefer external release hosting or CI-produced artifacts instead.

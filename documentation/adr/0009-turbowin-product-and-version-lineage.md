# ADR-0009: Adopt TurboWin branding and version 6.0.0 for the DWD release line

- Status: Accepted
- Date: 2026-11-11

## Context

The product has historically been released under two related names. The last version of
TurboWin without the suffix was `TurboWin V5.6`. KNMI's last release under the `TurboWin+`
name was `TurboWin+ V4.7 beta`. The `+` denoted the former product variant that added the
ability to communicate with devices.

The historical release names and ownership boundary are recorded in the KNMI binary archive
and the former KNMI repository README:

- [KNMI TurboWin binary archive](https://download.dwd.de/pub/turbowin/archive/knmi/binaries/)
- [Former KNMI TurboWin README](https://gitlab.com/KNMI-OSS/turbowin/turbowin/-/raw/master/readme.md)

## Decision

Use `TurboWin` as the sole current product name. The `TurboWin+` name remains only where it is
needed to describe historical releases, archived artifacts, or compatibility with legacy
installations and data.

Release the first DWD version as `TurboWin 6.0.0`. From that version onward, use Semantic
Versioning as the primary versioning convention, subject to the project's release and packaging
requirements.

The release lineage is therefore:

| Product/version | Role in the lineage |
| --- | --- |
| `TurboWin V5.6` | Last version of TurboWin without the suffix before the current release line |
| `TurboWin+ V4.6` | Last stable version released by KNMI under the `+` name |
| `TurboWin+ V4.7 beta` | Last version released by KNMI under the `+` name |
| `TurboWin 6.0.0` | First version released by DWD and the start of the current Semantic Versioning line |

## Consequences

Current UI, documentation, package metadata, and release artifacts use `TurboWin` and the new
version line. Historical references and legacy installation identifiers remain unchanged where
they are required for provenance, migration, or compatibility. The major-version jump makes the
branding, ownership, and versioning transition explicit, while future patch and minor releases can
follow Semantic Versioning.

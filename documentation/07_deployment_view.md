# Deployment View

TurboWin is installed locally on ship-based and fixed sea stations. Current packaging jobs target
Windows x64, Linux DEB on amd64/ARM64, and Linux RPM on amd64. Java portability does not guarantee
that every hardware driver or desktop integration works on every platform. Format 101 encoding
is implemented in Java and no native Format 101 helper is packaged with the application.

## Runtime and writable data

The build requires a Java 25 JDK. `jlink` creates the runtime image used by `jpackage` installers,
so those installations do not require a separately installed Java runtime. A standalone JAR is
not the same deployment: it does not bundle a runtime or all dependency JARs.

Current default writable roots from
[StartupEnvironment](../src/main/java/turbowin/StartupEnvironment.java) are:

| Platform branch | Data root |
| --- | --- |
| Windows | `C:\ProgramData\TurboWin` |
| Non-Windows | `/opt/turbowin/data` |

Desktop startup creates `logs` (including system logs) and `amver` below that root. Installation
and user permissions must permit writing there. Tests can use the `turbowin.data.dir` system
property to redirect startup data into a temporary directory. The active configuration is
`<data root>/configuration.txt`, with its previous valid version in `configuration.txt.bak`.
See the [configuration paths and migration guide](configuration_migration.md) for full paths,
upgrade steps, and manual import, and [persistence concepts](08_concepts.md#configuration-and-local-persistence)
for implementation details.

On startup without a `turbowin.data.dir` override, the application checks these legacy locations
in order, independently of the new application's installation directory:

1. On Windows, `TurboWin+\bin` under the `ProgramFiles(x86)`, `ProgramW6432`, and `ProgramFiles`
   environment directories, with conventional `C:\Program Files (x86)` and `C:\Program Files`
   fallbacks. Duplicate normalized paths are checked once.
2. On non-Windows systems, `/usr/local/bin/TurboWin+-<version>/turbowin_jws/bin`, highest numeric
   version first (final releases before prereleases of the same version), followed by
   `/usr/local/bin/turbowin_jws/bin`. Only the known versioned directory pattern is enumerated;
   startup does not search the entire filesystem.

These layouts reflect [reported legacy installation checks](legacy_storage_observations.md).
The launch working directory (`user.dir`) is not searched. Configurations from unlisted custom
locations can be imported explicitly.

If `configuration.txt` or its `.bak` backup already exists in the current data root, legacy
settings are not introduced beside it, even if the existing file is invalid. Otherwise migration selects the first usable legacy configuration, trying
that installation's `logs/configuration.txt` before its `configuration.txt`. Missing, unreadable,
malformed, empty, and unknown-only copies do not prevent trying the next copy or installation.
The selected file is parsed before copying; no live settings change during migration.

Migration rebases a saved logs folder that points to the source installation's own `logs`
directory to `<new data root>/logs`. It writes that validated snapshot only to
`<new data root>/configuration.txt`. Absolute custom logs folders retain their values, and relative
custom folders are resolved against the legacy `bin` directory to retain their location. Explicit
import does not perform this automatic rebasing. All later saves update only the data-root file,
regardless of the logs-folder setting, with the backup managed by the normal save workflow.

The migration also merges missing files from legacy `logs` and `amver` directories in discovery
order, even when current settings already exist. Custom external logs folders are not copied.
Existing destination files win, including conflicts between histories from multiple
installations; configuration files use the separate validated selection above. Source files are
retained for rollback and inspection. Application resources, launcher markers, and symlinked
history entries are not copied. Migration failures are reported and do not replace existing files;
normal startup still checks whether the new data root is writable.

The application currently starts in installed desktop ("offline") mode. This is distinct from
having no network connection: observation uploads, custom email, and external web resources still
use network services when selected.

## Build and packaging entry points

Use the Gradle wrapper and [build.gradle](../build.gradle). The generated application-version and
email-settings classes belong under `build/generated/`, not in the source tree. Compilation needs
all four non-empty build settings:

- `TURBOWIN_EMAIL_ENCRYPTION_KEY`
- `TURBOWIN_EMAIL_CHARACTER_ENCODING`
- `TURBOWIN_EMAIL_CIPHER_TRANSFORMATION`
- `TURBOWIN_EMAIL_AES_ENCRYPTION_ALGORITHM`

These are embedded by generation, not simply read as runtime environment overrides. Release
builds need the intended settings compatible with deployed encrypted email credentials. Dummy
values are for tests only; do not distribute a package built with those test values.

The platform scripts share [variables.json](../scripts/variables.json):

| Task | Entry point | Output |
| --- | --- | --- |
| JAR | `./scripts/task_linux.bash jar` | `build/libs/` |
| Full Linux tests and recording | `./scripts/task_linux.bash test` | Video/log plus Gradle HTML/XML reports |
| DEB on the runner's architecture | `./scripts/task_linux.bash deb` | `build/jpackage/` |
| RPM | `./scripts/task_linux.bash rpm` | `build/jpackage/` |
| Windows EXE | `powershell -File scripts/task_windows.ps1` | `build/jpackage/` |

The Linux script installs operating-system packages and expects an appropriate privileged
APT/microdnf environment and `JAVA_HOME`; it is intended for the CI containers, not an ordinary
unprivileged workstation command. The Windows script downloads the configured Liberica JDK and
WiX tools. Format 101 reference binaries used by compatibility tests are not production runtime
assets.

## Continuous integration

![Current build and test jobs](images/07_turbowin_pipeline.svg)

[GitHub Actions](../.github/workflows/) defines separate test and platform packaging workflows
on pushes/pull requests; the JAR workflow is manually dispatched. [.gitlab-ci.yml](../.gitlab-ci.yml)
defines equivalent task entry points in one `build` stage, with a manual JAR job. There is no
implemented signing or automatic release-publication stage in these definitions.

The shared Linux test task waits for Xvfb readiness, starts Openbox and screen recording, then runs
Gradle with `--no-daemon`. Gradle has a ten-minute timeout (with a thirty-second kill grace period).
Cleanup preserves the test exit status, stops the owned desktop processes, and finalizes the
recording on success or failure, with bounded recorder shutdown.

GitHub's test job has a twenty-minute job limit and attempts artifact upload even after failure:

- `screen_capture.webm`
- `test-run.log`
- `build/reports/tests/test/`
- `build/test-results/test/*.xml`

Files may be absent if startup fails before they are produced. Only the GitHub test workflow has
dummy fallbacks for missing email secrets; its packaging workflows still require configured
secrets. GitLab retries the test job twice and uploads the recording even on failure, but does not
currently declare all the additional report/log artifacts or the same job timeout/fallbacks.

For workstation commands and troubleshooting, see the [testing guide](contributing_and_testing.md).
Release artifacts should remain outside Git, following the [artifact policy](repository_cleanup.md).

# Contributor and Testing Guide

## Prerequisites and scope

Use a Java 21 JDK and the repository's Gradle wrapper. Application code/resources live under
`src/main/`, and JUnit/AssertJ Swing tests under `src/test/`. Format 101 compatibility tooling has
its own [instructions and campaigns](../contrib/format101/README.md); it is not automatically
verified by the root application's `test` task.

Compilation generates version and email-settings classes under `build/generated/`. Do not edit
or commit those generated classes. The four email build settings are required even when tests
do not send email. For local tests only, use:

```bash
export TURBOWIN_EMAIL_ENCRYPTION_KEY=dummy-key
export TURBOWIN_EMAIL_CHARACTER_ENCODING=UTF-8
export TURBOWIN_EMAIL_CIPHER_TRANSFORMATION=AES
export TURBOWIN_EMAIL_AES_ENCRYPTION_ALGORITHM=AES
```

These values satisfy generation; they are not real credentials or production-compatible encryption
settings. They do not by themselves prevent network calls: use test doubles and isolated fixtures.
Do not build distributable packages with these dummy constants. Release settings are described in
the [deployment view](07_deployment_view.md#build-and-packaging-entry-points).

## Running tests

On a workstation with an active desktop/display:

```bash
./gradlew test --no-daemon --console=plain
```

From PowerShell, set the same four names using `$env:NAME = 'value'` and invoke `./gradlew.bat`.
Use the appropriate real/virtual desktop for GUI tests.

For a focused test that does not need a visible desktop:

```bash
./gradlew test --no-daemon --console=plain \
  --tests turbowin.MintakaHistoricalRecordReaderTest
```

To run the full suite on Linux without an existing desktop, install Xvfb, Openbox, and the X11
libraries used by AWT. With `xvfb-run` available and the test settings above exported:

```bash
xvfb-run -a -s '-screen 0 1280x1024x24' bash -c '
  openbox &
  test_window_manager_pid=$!
  stop_test_window_manager() {
    test_exit=$?
    trap - EXIT
    kill "$test_window_manager_pid" 2>/dev/null || true
    wait "$test_window_manager_pid" 2>/dev/null || true
    exit "$test_exit"
  }
  trap stop_test_window_manager EXIT
  timeout --kill-after=30s 10m ./gradlew test --no-daemon --console=plain
'
```

`xvfb-run` also needs `xauth`. This workstation example does not record video; the privileged CI
task `./scripts/task_linux.bash test` installs its dependencies, waits for display readiness,
records the desktop, and cleans up its owned processes. See [CI behavior](07_deployment_view.md#continuous-integration).

Do not use `-Djava.awt.headless=true` as a substitute for a full run. Some UI tests skip via
assumptions; other characterization tests construct dialogs and fail in a headless environment.
Check failures and skipped tests, not just whether a filtered helper test passed. Use `--rerun-tasks`
when a fresh run is needed rather than Gradle's up-to-date result.

Reports are generated at:

- `build/reports/tests/test/index.html`
- `build/test-results/test/TEST-*.xml`

The CI task also creates `test-run.log` and `screen_capture.webm`. Do not commit generated reports,
recordings, or release outputs.

## Test isolation and completion

Tests that touch `main`, `main_RS232_RS422`, or `my*` classes must save and restore the relevant
global values and component references. Establish the assumptions the test requires explicitly;
do not rely on the defaults left by an earlier test. Use temporary directories for logs, history,
and configuration, and restore changed time zones, date formats, and system properties.

[AbstractUiTest](../src/test/java/turbowin/AbstractUiTest.java) redirects startup data using
`turbowin.data.dir`, establishes manual startup settings, dismisses modal startup dialogs,
restores the repaint manager, and releases the static single-instance socket on teardown.
Use its lifecycle when appropriate. Disposing a frame is not sufficient cleanup for application
resources. Do not run tests that share these globals concurrently without first isolating them.

For asynchronous workflows, wait for the side effect that actually defines completion and allow
the EDT callback to finish before teardown restores state. A transport-called flag can become
true before `done()` logs/resets the observation. Swing mutations/assertions should use the EDT
or AssertJ Swing fixtures. Use bounded waits and targeted dialog handling rather than unbounded
sleeps or dismissing unrelated windows.

When using clock-dependent sensor fixtures, honor the reader's hourly-file margin and timestamp
format. Characterize age boundaries and hour rollover instead of assuming the current minute
always belongs to the selected file.

## Preserving legacy contracts

Before changing a workflow, understand its parent/current code, callers, tests, and relevant
history. Behavior preservation and preservation of domain knowledge are separate questions.
Tests should characterize meaningful contracts such as:

- Code meanings, missing markers, units, coordinate padding/hemispheres, and rounding.
- Exact field widths, comma counts, checksums, timestamps, and record-selection order.
- Validation ordering, defaults, confirmations, and intentionally strange legacy handling.
- Logging, silent rejection, swallowed exceptions, and which values are retained or cleared.
- Output failure paths that still log/reset, and callbacks that run after background work.

Useful regression examples include the [historical Mintaka reader tests](../src/test/java/turbowin/MintakaHistoricalRecordReaderTest.java)
and [StarX dashboard parser tests](../src/test/java/turbowin/Rs232DashboardRecordParserTest.java).
Do not silently replace file-order invalidation with "latest valid", add diagnostics to an
intentionally silent rejection, or merge empty-field and whitespace handling.

Keep corrections scoped to the behavior under review. Characterize a suspected regression before
fixing it where practical, preserve comments explaining why, and describe intentional behavior
changes explicitly. Do not infer that an odd legacy rule is a bug from style alone.

## Formatting and documentation

Java formatting is configured with Spotless/google-java-format. `./gradlew spotlessCheck` checks
formatting; `./gradlew spotlessApply` rewrites configured Java files, so inspect its scope and do
not include unrelated formatting in a focused change.

Keep architecture text organized around responsibilities and contracts, not a class catalogue
or commit count. Check relative links and update both PlantUML sources and generated SVGs when
changing a diagram. From the repository root, using PlantUML 1.2025.4:

```bash
java -jar /path/to/plantuml.jar -charset UTF-8 -tsvg \
  documentation/images/05_building_block_view.puml \
  documentation/images/07_turbowinplus_pipeline.puml
```

The component diagram selects PlantUML's Smetana layout, so these diagrams do not require an
external Graphviz installation. The PlantUML tool itself need not be committed to the repository.

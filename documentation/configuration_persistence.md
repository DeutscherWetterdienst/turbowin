# Configuration persistence

TurboWin keeps its active settings in mutable static fields in `main`, such as `ship_name`,
`wind_source`, and serial/email/dashboard settings. `configuration.txt` stores those settings
between runs. The `configuratie_regels` array (Dutch for "configuration lines") is an intermediate
text representation, not a second independent settings model.

## What happened to muffins?

Java Web Start provided persistent local storage, commonly called "muffins", through its JNLP
persistence service. Older TurboWin comments describe saving metadata there and keeping two
`configuration.txt` copies as backups. The current installed application has no active muffin
reader or writer. Its persistence uses the two text files directly. Historical muffin paths,
`read_muffin()` references in comments, and `MUFFIN_LINE_SEPARATOR` do not describe an active
storage mechanism. Renaming launcher markers does not restore muffin support.

## Loading and saving

```mermaid
flowchart TD
    A[Startup selects writable data root] --> B[Initialize logs_dir to data root / logs]
    B --> C[Try logs configuration, then data configuration on failure]
    C --> D[Background worker reads and parses temporary settings]
    D --> E[EDT applies first valid copy or retains existing settings]
    E --> F[EDT callback finalizes UI and connections]
    G[Settings dialogs update globals] --> H[Save snapshots lines and paths into one ordered queue]
    H --> I[Background write to data_dir / configuration.txt]
    I --> J[Background write to logs_dir / configuration.txt]
```

`main.lees_configuratie_regels()` and `main.schrijf_configuratie_regels()` delegate through
`ConfigurationPersistenceWorkflow` to `ConfigurationReadWorkflow` and `ConfigurationWriteWorkflow`.
`ConfigurationManager` converts between globals and text lines; `ConfigurationFileStore` performs
file I/O. The same line format is used by maintenance import/export.

The data root defaults to `C:\ProgramData\TurboWinPlus` on Windows and `/opt/turbowinplus/data`
elsewhere. `-Dturbowin.data.dir=...` overrides it and disables automatic legacy migration.
Otherwise startup checks `user.dir` and the known Windows, Debian, and versioned RPM installation
layouts before loading; see the [deployment view](07_deployment_view.md#runtime-and-writable-data).
Current startup always selects installed desktop ("offline") mode, which still permits networking.

The reader tries `configuration.txt` under `logs_dir` first, then under `data_dir` if the first
copy is missing, unreadable, or contains malformed recognized values. Empty files and files with
no recognized settings are also rejected. Each attempt uses a separate temporary line array and
parses all recognized values before applying any of them. It does not merge copies or compare
modification dates. Missing entries in a valid partial file preserve existing global values,
normally field defaults on a fresh process or previous values on a theme restart. If neither copy
is usable, both globals and the existing line array are retained. Diagnostics identify the selected
file and recovery reasons. Rejected values are not included in parser diagnostics.
If both directory names are empty, no read or startup finalization is scheduled.
Loading does not itself write or repair a configuration file.

Startup initially sets `logs_dir` to `<data root>/logs`, but applying a saved `logs dir` entry can
replace it, even in offline mode. Thus the directory used to locate the startup file and the
directory used for subsequent logs/saves can differ. `data_dir` is selected by startup, not loaded
from a configuration entry.

Automatic migration treats installation-local logs folders separately: it rewrites only a path
pointing to the legacy installation's own `logs` directory to the new default. Custom folders
retain their location. For custom folders, migration creates only the data-root configuration,
avoiding a stale preferred copy in the default logs directory after later saves. Existing modern
configuration files are preserved. Explicit maintenance import continues to apply the supplied
logs-folder setting without rebasing it.

After the background read and parsing, the Swing completion callback retrieves the worker result,
applies an accepted file, and reports diagnostics. It then logs startup, cleans old system logs,
updates station identity, checks metadata, creates menus, initializes connections, and updates
menu/toolbar availability. Startup finalization also runs when neither copy is usable or the
background worker fails.

Saving first rebuilds the line array from globals and copies the lines and both destination paths.
Snapshot capture and submission are synchronized so each request places both writes together into
one background queue, data first and then logs. Each non-empty destination is attempted using that
same snapshot. Subsequent changes to the global array or paths cannot change an already queued
save. Older requests finish their write attempts before newer requests begin, preventing an older
snapshot from overwriting a newer one within this process.

Each write uses a uniquely named temporary file in the destination directory, closes it, then
atomically replaces `configuration.txt`. A failed write or replacement removes the temporary file
and leaves the previous configuration in place. Filesystems that do not support atomic replacement
report a save error; there is no non-atomic fallback. Existing configuration symlinks are followed
so their targets are replaced, and existing POSIX access modes are preserved. New files retain
regular-file permissions subject to the process umask on POSIX filesystems.

Completion of the save method does not mean the files are finished. The two copies are not a
single transaction: one can succeed while the other fails. A failed copy does not block the other
copy or later saves. Data-root write errors go to standard output; log-directory write errors
display a warning on the EDT referring to the captured destination, without blocking the save
queue. Ordering applies within this process. Atomic replacement protects against partial file
contents; it does not add power-loss durability or automatic recovery of temporary files left by
a process crash.

## Saving before exit

Window close, menu Exit, and tray Exit use the same shutdown handler. After the existing exit
confirmation, it queues a final snapshot of current settings and waits for the configuration save
queue. A progress dialog prevents further UI edits while Swing continues processing events.
Saves submitted by an already-running workflow during this wait are also drained before the
completion callback. Repeated exit requests do not start additional shutdown attempts.
The progress dialog also offers Cancel exit, allowing normal use to resume if storage is slow.
Cancellation does not interrupt an in-progress file write, and its eventual completion cannot
trigger the cancelled exit.

The latest save request must succeed at every configured destination before shutdown proceeds
automatically. An older failed snapshot does not block exit if the final snapshot succeeds.
Failures identify the affected configuration files and offer Retry saving, Cancel exit, or Exit
without saving. Closing that decision dialog cancels exit. Retry captures current settings again
and attempts both copies; cancelling keeps connections and the application running and leaves
the save queue available. Ordinary log-copy warnings from these attempts are suppressed in favor
of the shutdown decision dialog. With no configured destinations, exit also requires a decision.

Only after successful saving or an explicit choice to exit without saving does the handler close
serial connections, remove the tray icon if needed, log shutdown, and exit. This waits for
configuration writes, not every background operation or system-log write. Forced termination,
crashes, and the duplicate-instance startup rejection do not drain the daemon save queue.

## File format

The file contains labeled text lines with a fixed 21-character prefix, including padding, a colon,
and a space. For example:

```text
ship name          : Example Ship
wind source        : estimated; true speed and true direction
```

`ConfigurationManager.fillConfigurationLines()` writes wind source at index `7`; index `2` is
the legacy call-sign label. Call-sign and masked-call-sign values are deliberately omitted from
these legacy entries and are not applied when reading them.

The array has 100 entries; the current builder assigns indices `0` through `88`. The writer skips
null/empty entries, so array positions need not survive a file round trip. The reader accepts at
most 100 physical lines. Applying settings scans all entries and matches the padded label using
substring matching, then takes the value starting at character offset 21. It does not dispatch by
array index or parse an arbitrary `key: value` syntax. Preserve the label padding when editing a
file. Recognized repeated settings are applied in file order, so the later value wins if parsing
completes. Unknown lines are ignored; malformed recognized values reject the entire candidate
before any of its settings are applied. Legacy boolean coercion and serial-code conversions remain
unchanged; validation does not impose new ranges on values that previously remained strings.

The store uses `FileReader`/`FileWriter` default character encoding and platform line endings.
Some values are parsed as integers or booleans; others remain strings. After applying settings,
the manager selects effective serial-port names and maps historical `python_email` to
`jakarta_email`. Email password fields already contain encrypted strings when handed to the
configuration builder; the file store does no encryption of its own.

## Import and export

Maintenance and station-data imports use `ConfigurationImportReader` to read up to 100 lines into
a private buffer and parse recognized values in the background. No live settings or shared lines
change while reading or parsing. A read error, malformed recognized value, or file with no
recognized settings rejects the import without applying or saving it. Diagnostics report the
failure without including rejected values. Missing settings in a valid older partial file retain
their current values, and unknown labels are ignored.

Maintenance import additionally requires the ship-name and call-sign labels in that same buffer;
it reads the source only once. The check requires the labels, not non-empty values. It does not
exclude `logs dir`. Its EDT completion callback applies a valid import, starts saving, reports
success or failure, and refreshes the maintenance display in either case. The success message
confirms that the import was applied and saving was queued; it does not wait for saving to finish.

Station-data import uses the same settings parser, so it can apply settings beyond station
metadata. It excludes `logs dir` in offline mode, captured when the import starts, and does not
require the maintenance identity labels. Its EDT completion callback applies a valid import,
starts saving, and refreshes the station form. Failure is logged without saving or refreshing the
form. An import source that is also a save destination is read and closed before saving begins.
The configuration writes remain asynchronous: refreshing either form is not proof that the files
have finished saving, and a later save error does not roll back the applied import.

Maintenance display rebuilds the array from current globals. Maintenance export writes the
current array to the chosen file without first rebuilding it, then refreshes the display. An
export therefore reflects the array available when its worker runs, which can lag unsaved global
changes. Accepted imports update the shared array on the EDT; exports and ordinary saves use that
array as described above.

These details describe the existing implementation. In particular, fallback on a missing copy,
recovery from a failed read, and completion of asynchronous saving are separate behaviors.

# Configuration paths and migration

Use this guide when upgrading an older installation or locating settings. The
[storage observations](legacy_storage_observations.md) record which released installers were
manually checked; the [persistence reference](configuration_persistence.md) explains validation,
saving, backup recovery, and import/export in detail.

## Current locations

The data directory is independent of the application's installation directory and launch working
directory. Its default paths are:

| Purpose | Windows | Linux / non-Windows branch |
| --- | --- | --- |
| Active settings | `C:\ProgramData\TurboWinPlus\configuration.txt` | `/opt/turbowinplus/data/configuration.txt` |
| Previous valid settings | `C:\ProgramData\TurboWinPlus\configuration.txt.bak` | `/opt/turbowinplus/data/configuration.txt.bak` |
| Default operational logs | `C:\ProgramData\TurboWinPlus\logs\` | `/opt/turbowinplus/data/logs/` |
| AMVER data | `C:\ProgramData\TurboWinPlus\amver\` | `/opt/turbowinplus/data/amver/` |

There is one active configuration. Startup tries that file first and validates the backup only
if the active file cannot be used. Saves update the active file and, when its valid contents
change, retain the previous contents in `.bak`. A `configuration.txt` in a logs folder is never
used as a current startup fallback or updated by current saves.

The saved `logs folder` setting can redirect operational logs to a custom directory. It does not
move the active configuration or its backup. Both remain in the selected data root. Startup
creates the default `logs` and `amver` directories; the account running TurboWin needs write access
to the data root and any selected custom logs directory.

The JVM option `-Dturbowin.data.dir=/absolute/path/to/data` selects another data root. On Windows,
use a Windows path and quote the option if it contains spaces. This option also disables all
automatic legacy migration, including history copying. It is useful for isolated tests and
explicitly managed deployments; it does not select a legacy source directory.

## Legacy locations that are searched

For each legacy installation's `bin` directory, migration tries these files in order:

1. `bin/logs/configuration.txt`
2. `bin/configuration.txt`

The same order applies on Windows using backslashes. A usable logs copy wins even if the bin-root
copy has a later modification time. The copies are not merged and timestamps are not compared.

| Legacy layout | Installation bin directory | New active configuration |
| --- | --- | --- |
| Windows | `C:\Program Files (x86)\TurboWin+\bin` or `C:\Program Files\TurboWin+\bin` | `C:\ProgramData\TurboWinPlus\configuration.txt` |
| Debian | `/usr/local/bin/turbowin_jws/bin` | `/opt/turbowinplus/data/configuration.txt` |
| RPM | `/usr/local/bin/TurboWin+-<version>/turbowin_jws/bin` | `/opt/turbowinplus/data/configuration.txt` |

Windows discovery checks the `ProgramFiles(x86)`, `ProgramW6432`, and `ProgramFiles` environment
directories, in that order, then the two conventional `C:` locations above. Duplicate normalized
paths are checked once. Linux discovery checks versioned RPM directories in descending numeric
version order, with final releases before prereleases of the same version, then the Debian
layout. Thus `4.10.0` precedes `4.9.0`.

Discovery is limited to these layouts. It does not search `user.dir`, arbitrary custom installation
folders, user profiles, AppData, or Windows VirtualStore. The Debian path is supported for
discovery, but the Debian 4.6 startup failed on permissions before a saved configuration
could be confirmed. See the observations for the limits of the manual evidence.

## What happens during startup

Without a data-root override, migration runs before current settings are loaded:

1. If either the current `configuration.txt` or `configuration.txt.bak` exists, skip importing
   legacy settings. This existence check applies even if that file is invalid: migration does not
   silently replace current data. Normal startup then validates the active file and backup.
2. Otherwise, inspect installations in discovery order. Within each installation, try its logs
   configuration, then its bin-root configuration. Missing, unreadable, malformed, empty, and
   unknown-only files are skipped. The first valid file supplies the entire configuration;
   recognized values must parse before it is selected.
3. Adjust its logs-folder path as described below and write the selected settings only to
   `<new data root>/configuration.txt`. Migration creates no second configuration in `logs` and
   no initial `.bak`; ordinary later saves manage the backup.
4. Merge missing files from each discovered installation's `logs` and `amver` directories into the
   new root's corresponding directories. This history merge still runs when existing current
   settings prevented configuration migration. Existing destination files win; the first source
   wins conflicts between legacy installations. Legacy `logs/configuration.txt` is excluded from
   this history copy. Custom external logs directories are not copied automatically.
5. Continue normal startup using current settings, backup recovery, or defaults as appropriate.

Source files are retained. Application help, launchers, marker files, and symlinked history
entries are not migrated. Repeated starts can copy newly discovered missing history files but
never overwrite existing destination files. Migration is best-effort, rather than a transaction
covering settings and histories: errors appear as `[MIGRATION]` diagnostics and startup continues.
If copying selected settings fails, a later start can retry; startup still requires writable
current directories. `[CONFIGURATION]` diagnostics identify files loaded or rejected.

## How logs-folder paths change

| Stored legacy value | Value after automatic migration |
| --- | --- |
| The source installation's own `logs` directory, including relative `logs` | `<new data root>/logs` |
| An absolute custom logs directory | Preserved |
| A non-empty relative custom logs directory | Resolved against the source installation's bin directory |

For example, an RPM 4.6 configuration pointing to
`/usr/local/bin/TurboWin+-4.6.0/turbowin_jws/bin/logs` is rewritten to
`/opt/turbowinplus/data/logs`. A custom `/srv/ship-logs` stays unchanged. A custom `../ship-logs`
is resolved relative to the old `bin` directory so it continues to refer to its original location.
Empty or unresolvable custom values are preserved. Explicit imports do not perform this rebasing.

## Custom installations and manual import

If discovery cannot find an old installation, use maintenance-data import and select its
`configuration.txt` explicitly. Moving the launch working directory to that installation does
not enable discovery. Maintenance import requires the legacy ship-name and call-sign labels,
validates the file before applying it, and queues a save to the current data root. A success
message confirms application of settings, not completion of the asynchronous save.

Maintenance import preserves the supplied logs-folder setting exactly. Review that setting when
moving between machines or operating systems; it may still point into the old installation.
Station-data import has different rules: in desktop/offline mode it preserves the currently
selected logs directory. Neither import copies operational histories. Copy any additional
histories separately if needed, preserving existing destination files.

If current settings already exist, an explicit import is the supported way to intentionally
replace them with selected legacy settings. There is no need to remove the active file or backup
to force automatic migration. After saving, check the active file at the current path and any
save-error messages before retiring the old installation.

When startup recovers from `.bak`, it displays a warning and leaves the active file untouched.
The next save repairs it, preserving invalid existing contents as a unique
`configuration-*.damaged` file beside the active file. Keep that inspection file if investigating
corruption. The backup protects a previous valid version; it is not an archive of all old settings.

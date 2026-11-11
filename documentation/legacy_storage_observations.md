# Legacy storage observations

| Package | Tested OS | Observed layout |
| --- | --- | --- |
| `setup_turbowin+_jpms_4_6_hl.exe` | Windows 11 x64 | `C:\Program Files (x86)\TurboWin+\bin\logs\configuration.txt`; no configuration directly in `bin` in the directory listing |
| `setup_turbowin+_jpms_4_7_beta_hl.exe` | Windows 11 x64 | Behave like the JPMS 4.6 installer |
| `setup_turbowin%2B_4_6_hl.exe` | Windows 11 x64 | Behave like the JPMS 4.6 installer |
| `TurboWin+_4.6.0-1.deb` | Debian 13 amd64 | Installation under `/usr/local/bin`; startup failed to create `/usr/local/bin/turbowin_jws/bin/logs` because of write permissions. A saved configuration was not confirmed |
| `TurboWin+-4.6.0-1.x86_64.rpm` | Fedora Workstation 44 x86_64 | Both `configuration.txt` and `logs/configuration.txt` under `/usr/local/bin/TurboWin+-4.6.0/turbowin_jws/bin/`; the two files were identical |
| `TurboWin+-4.5.0-1.x86_64.rpm` | Fedora Workstation 44 x86_64 | Working installation at `/usr/local/bin/TurboWin+-4.5.0/turbowin_jws/bin/`; individual configuration copies were not separately listed |

The Windows configuration explicitly stored the installation's `bin\logs` directory in its
`logs folder` entry. It used the existing padded-label format, including empty settings and
legacy serial values such as parity `99` and data bits `0`.

These checks establish the layouts above, not that every installer or launch method uses them.
Elevation, directory permissions, shortcut working directories, and VirtualStore redirection were
not recorded. Other Windows and Linux versions from the proposed test matrix remain unconfirmed.

Automatic discovery and migration are described in the
[configuration paths and migration guide](configuration_migration.md) and
[deployment view](07_deployment_view.md#runtime-and-writable-data). Explicit maintenance import
retains its separate behavior, including importing an explicitly supplied logs-folder setting.

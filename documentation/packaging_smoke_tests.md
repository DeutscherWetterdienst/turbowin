# Packaging smoke tests

The packaging CI jobs build and exercise each installer in a disposable runner:

| Artifact | CI runner | Test |
| --- | --- | --- |
| `deb-amd64` | `ubuntu-latest` / Debian container | install, launch, remove, reinstall, purge |
| `deb-arm64` | native `ubuntu-24.04-arm` / Debian container | install, launch, remove, reinstall, purge |
| `rpm-amd64` | `ubuntu-latest` / UBI container | install, launch, remove, reinstall |
| `exe-amd64` | `windows-latest` | silent install, launch, uninstall, reinstall |

Linux tests also extract package metadata and maintainer scripts, check the installed file list,
and remove the optional, package-created data directory before exercising removal. This catches
failures caused by missing files or assumptions about package state without altering the internal
launcher/configuration state used by the maintainer scripts. Logs and package diagnostics are
written below `build/package-smoke/` and uploaded by CI even when the test fails.

Run a test locally after building its installer (as root or in a disposable VM/container):

```bash
./scripts/package_smoke_test_deb.bash build/jpackage/TurboWin-6.0.0.deb
./scripts/package_smoke_test_rpm.bash build/jpackage/TurboWin-6.0.0.rpm
```

To set the Debian package maintainer email without storing it in Git, export
`TURBOWIN_EMAIL_DEB_MAINTAINER` before running the DEB packaging task. GitHub Actions supplies it
from the `TURBOWIN_EMAIL_DEB_MAINTAINER` secret; GitLab CI can define a masked project variable with
the same name. The DEB packaging task fails fast if it is unset or empty.

On Windows PowerShell, run the EXE test in an elevated disposable runner:

```powershell
.\scripts\package_smoke_test_exe.ps1 build\jpackage\TurboWin-6.0.0_64-bit.exe
```

The application has no dedicated headless mode. Linux launch checks use `xvfb-run`; Windows starts
the real GUI executable and stops it after confirming that it remains alive. Upgrade testing is
ready for a retained older artifact by setting `TURBOWIN_OLDER_PACKAGE` to its path; CI does not set
it until an older package is published.

To add another format or architecture, add a format-specific test script, pass the generated
artifact to it after the existing packaging task, and upload its diagnostic directory with the
installer. Keep architecture tests on native runners; inspecting an ARM64 package on AMD64 is not
a substitute for installing it.

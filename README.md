# TurboWin+

TurboWin+ is software for collecting high-quality, real-time meteorological observation data on ships.

## Repository policy

This repository contains source code, build scripts, documentation, and required runtime resources.

## Building

### Linux

```bash
./scripts/task_linux.bash test
./scripts/task_linux.bash jar
./scripts/task_linux.bash deb
./scripts/task_linux.bash rpm
```

### Windows

```powershell
powershell -File scripts/task_windows.ps1
```

## CI

CI workflows are available in:

- `.github/workflows/`
- `.gitlab-ci.yml`

## Documentation

See [`documentation/`](documentation/) for project and architecture documentation.

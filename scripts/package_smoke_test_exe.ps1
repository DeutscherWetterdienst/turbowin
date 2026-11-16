[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string] $Artifact,
    [Parameter(Position = 1)]
    [string] $DiagnosticDirectory = "build/package-smoke/exe"
)

$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path $DiagnosticDirectory | Out-Null
$Artifact = (Resolve-Path $Artifact).Path
Start-Transcript -Path (Join-Path $DiagnosticDirectory "operations.log") -Force | Out-Null

$installDirectory = Join-Path $env:ProgramFiles "TurboWin"
$application = Join-Path $installDirectory "TurboWin.exe"

function Get-TurboWinUninstallEntry {
    $roots = @(
        "HKLM:\Software\Microsoft\Windows\CurrentVersion\Uninstall\*",
        "HKLM:\Software\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall\*"
    )
    Get-ItemProperty $roots -ErrorAction SilentlyContinue |
        Where-Object { $_.DisplayName -eq "TurboWin" } |
        Select-Object -First 1
}

function Assert-Installed {
    if (-not (Test-Path $installDirectory -PathType Container)) { throw "Install directory is missing: $installDirectory" }
    if (-not (Test-Path $application -PathType Leaf)) { throw "Application executable is missing: $application" }
    foreach ($path in @("app", "runtime")) {
        if (-not (Test-Path (Join-Path $installDirectory $path))) { throw "Installed support directory is missing: $path" }
    }
    $entry = Get-TurboWinUninstallEntry
    if (-not $entry) { throw "TurboWin is missing from the Windows uninstall registry" }
    if ($entry.InstallLocation.TrimEnd('\') -ne $installDirectory.TrimEnd('\')) {
        throw "Uninstall registry points to an unexpected location: $($entry.InstallLocation)"
    }
}

function Invoke-Installer([string] $Path, [string] $LogName) {
    Write-Host "== $LogName =="
    $logPath = Join-Path $DiagnosticDirectory "$LogName-wix.log"
    # jpackage EXE installers wrap an MSI. Pass MSI's quiet options through the wrapper;
    # /S is an Inno Setup switch and can leave the installer waiting for interactive input.
    $process = Start-Process -FilePath $Path -ArgumentList @("/qn", "/norestart", "/log", $logPath) -PassThru
    if (-not $process.WaitForExit(120000)) {
        taskkill.exe /PID $process.Id /T /F | Out-File (Join-Path $DiagnosticDirectory "$LogName-timeout.log")
        throw "Installer timed out after 120 seconds"
    }
    $process.ExitCode | Out-File (Join-Path $DiagnosticDirectory "$LogName.log")
    if ($process.ExitCode -ne 0) { throw "Installer exited with status $($process.ExitCode)" }
}

function Invoke-Uninstaller([string] $LogName) {
    Write-Host "== $LogName =="
    $entry = Get-TurboWinUninstallEntry
    if (-not $entry) { throw "TurboWin uninstall registry entry is missing" }
    $match = [regex]::Match([string]$entry.UninstallString, '\{[0-9A-Fa-f-]{36}\}')
    if (-not $match.Success) { throw "Could not find the MSI product code in: $($entry.UninstallString)" }
    $logPath = Join-Path $DiagnosticDirectory "$LogName-wix.log"
    $process = Start-Process -FilePath "msiexec.exe" -ArgumentList @("/x", $match.Value, "/qn", "/norestart", "/log", $logPath) -PassThru
    if (-not $process.WaitForExit(120000)) {
        taskkill.exe /PID $process.Id /T /F | Out-File (Join-Path $DiagnosticDirectory "$LogName-timeout.log")
        throw "Uninstaller timed out after 120 seconds"
    }
    $process.ExitCode | Out-File (Join-Path $DiagnosticDirectory "$LogName.log")
    if ($process.ExitCode -ne 0) { throw "Uninstaller exited with status $($process.ExitCode)" }
    for ($attempt = 0; $attempt -lt 30 -and (Test-Path $installDirectory); $attempt++) { Start-Sleep -Seconds 1 }
    if ((Get-TurboWinUninstallEntry)) { throw "TurboWin remains registered after uninstall" }
    if (Test-Path $installDirectory) { throw "Installation directory remains after uninstall: $installDirectory" }
}

function Invoke-ApplicationSmoke {
    $stdout = Join-Path $DiagnosticDirectory "application.stdout.log"
    $stderr = Join-Path $DiagnosticDirectory "application.stderr.log"
    $process = Start-Process -FilePath $application -RedirectStandardOutput $stdout -RedirectStandardError $stderr -PassThru
    Start-Sleep -Seconds 10
    if ($process.HasExited -and $process.ExitCode -ne 0) { throw "Application exited during startup with status $($process.ExitCode)" }
    if (-not $process.HasExited) { Stop-Process -Id $process.Id -Force }
}

try {
    Invoke-Installer $Artifact "install"
    Assert-Installed
    Invoke-ApplicationSmoke
    Invoke-Uninstaller "uninstall"

    Invoke-Installer $Artifact "reinstall"
    Assert-Installed
    Invoke-Uninstaller "uninstall-after-reinstall"

    if ($env:TURBOWIN_OLDER_PACKAGE) {
        Invoke-Installer $env:TURBOWIN_OLDER_PACKAGE "install-older"
        Invoke-Installer $Artifact "upgrade"
        Assert-Installed
        Invoke-Uninstaller "uninstall-after-upgrade"
    }
    Write-Host "EXE lifecycle smoke test passed"
}
finally {
    try { Stop-Transcript | Out-Null } catch {}
    if (Test-Path $installDirectory) {
        try {
            $entry = Get-TurboWinUninstallEntry
            $match = [regex]::Match([string]$entry.UninstallString, '\{[0-9A-Fa-f-]{36}\}')
            if ($match.Success) {
                $cleanupProcess = Start-Process -FilePath "msiexec.exe" -ArgumentList @("/x", $match.Value, "/qn", "/norestart") -PassThru
                if (-not $cleanupProcess.WaitForExit(120000)) { taskkill.exe /PID $cleanupProcess.Id /T /F | Out-Null }
            }
        } catch {}
    }
}

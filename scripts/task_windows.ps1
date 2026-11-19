# Determine the directory of the current script.
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$variablesPath = Join-Path $scriptDir "variables.json"

if (Test-Path $variablesPath) {
    $variables = Get-Content -Raw -Path $variablesPath | ConvertFrom-Json
    $env:APP_NAME = $variables.APP_NAME
    $env:LICENSE_FILE = $variables.LICENSE_FILE
    $env:VENDOR = $variables.VENDOR
    $env:MODULE_NAME_MAIN_CLASS = $variables.MODULE_NAME_MAIN_CLASS
    $env:ICON_PATH_WINDOWS = $variables.ICON_PATH_WINDOWS
    $env:JDK_WINDOWS_VERSION = $variables.JDK_WINDOWS_VERSION
    $env:WIX_BINARIES_WINDOWS_URL = $variables.WIX_BINARIES_WINDOWS_URL
    $env:WIX_BINARIES_WINDOWS_SHA256 = $variables.WIX_BINARIES_WINDOWS_SHA256
} else {
    Write-Error "Error: $variablesPath not found."
    exit 1
}

Write-Output "Running exe packaging task on Windows..."

Write-Output "WIX_BINARIES_WINDOWS_URL: $env:WIX_BINARIES_WINDOWS_URL"
$toolsDir = Join-Path (Get-Location) 'build/package-tools'
New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
$jdkArchive = Join-Path $toolsDir 'jdk_x64_windows.zip'
$jdkDirectory = Join-Path $toolsDir 'jdk_x64_windows'
$wixArchive = Join-Path $toolsDir 'wix-binaries.zip'
$wixDirectory = Join-Path $toolsDir 'wix-binaries'

# Use a preinstalled Java 25 when the runner provides one. GitHub Actions supplies Temurin via
# setup-java; GitLab-hosted Windows runners provide OpenJDK. Keep the BellSoft download as a
# fallback for local or older runners.
$javaHomeCandidate = $env:JAVA_HOME
if (-not $javaHomeCandidate) {
    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($javaCommand) {
        $javaHomeCandidate = Split-Path (Split-Path $javaCommand.Source -Parent) -Parent
    }
}

$java25Available = $false
if ($javaHomeCandidate -and (Test-Path "$javaHomeCandidate/bin/java.exe")) {
    $javaVersionOutput = (& "$javaHomeCandidate/bin/java.exe" -version 2>&1 | Out-String)
    $java25Available = $javaVersionOutput -match 'version "25([.]|[-])'
}

if (-not $java25Available) {
    # Set up the fallback JDK download URL.
    $env:JDK_X64_WINDOWS_URL = "https://download.bell-sw.com/java/$($env:JDK_WINDOWS_VERSION)/bellsoft-jdk$($env:JDK_WINDOWS_VERSION)-windows-amd64.zip"
    Write-Output "Using fallback JDK: $env:JDK_X64_WINDOWS_URL"
    Invoke-WebRequest -Uri $env:JDK_X64_WINDOWS_URL -OutFile $jdkArchive
}

# WiX is needed by jpackage for the Windows installer.
Invoke-WebRequest -Uri $env:WIX_BINARIES_WINDOWS_URL -OutFile $wixArchive
$actualWixSha256 = (Get-FileHash -Algorithm SHA256 -Path $wixArchive).Hash.ToLowerInvariant()
if ($actualWixSha256 -ne $env:WIX_BINARIES_WINDOWS_SHA256.ToLowerInvariant()) {
    throw "WiX archive checksum mismatch. Expected $env:WIX_BINARIES_WINDOWS_SHA256 but got $actualWixSha256."
}
Write-Output "WiX archive SHA-256 verified: $actualWixSha256"

# List downloaded files
if (-not $java25Available) {
    Get-Item $jdkArchive | Select-Object Name, Length
}
Get-Item $wixArchive | Select-Object Name, Length

# Expand the downloaded archives
if (-not $java25Available) {
    Expand-Archive -Path $jdkArchive -DestinationPath $jdkDirectory -Force
}
Expand-Archive -Path $wixArchive -DestinationPath $wixDirectory -Force

# Set JAVA_HOME to the selected Java 25 installation.
if (-not $java25Available) {
    $VersionWithoutBuild = $env:JDK_WINDOWS_VERSION -split "\+" | Select-Object -First 1
    $javaHomeCandidate = Join-Path $jdkDirectory "jdk-$VersionWithoutBuild"
}
$env:JAVA_HOME = $javaHomeCandidate
Write-Output "JAVA_HOME is set to: $env:JAVA_HOME"

# Copy WiX binaries into the JDK bin folder
Copy-Item -Path "$wixDirectory/*" -Destination "$env:JAVA_HOME/bin" -Recurse -Force

# Run some checks
& "$env:JAVA_HOME/bin/light.exe" -help
& "$env:JAVA_HOME/bin/candle.exe" -help
& "$env:JAVA_HOME/bin/java.exe" --version
& "$env:JAVA_HOME/bin/jlink.exe" --version
& "$env:JAVA_HOME/bin/jpackage.exe" --version

# Get the app version from Gradle
$env:APP_VERSION = (& .\gradlew.bat printVersion -q | Select-Object -Last 1).Trim()
$buildSuffix = (& .\gradlew.bat printBuildArtifactSuffix -q | Select-Object -Last 1).Trim()
Write-Output "App version is $env:APP_VERSION"

# Run jlink via Gradle
.\gradlew.bat jlink --stacktrace --info
.\gradlew.bat --stop

# Run jpackage to create the exe installer
if (Get-ChildItem -Path 'build/jpackage' -Filter '*.exe' -File -ErrorAction SilentlyContinue) {
    throw 'Move existing EXE installers out of build/jpackage before packaging.'
}
& "$env:JAVA_HOME/bin/jpackage.exe" `
  --verbose `
  --type exe `
  --runtime-image build/image `
  --module $env:MODULE_NAME_MAIN_CLASS `
  --java-options '-Dapp.dir=$APPDIR' `
  --dest build/jpackage `
  --name $env:APP_NAME `
  --app-version $env:APP_VERSION `
  --license-file $env:LICENSE_FILE `
  --vendor "$($env:VENDOR)" `
  --icon $env:ICON_PATH_WINDOWS `
  --win-upgrade-uuid "73902787-F60E-40DE-B64B-CA593EE0A5E0" `
  --win-menu `
  --win-menu-group $env:APP_NAME `
  --win-shortcut `
  --win-dir-chooser
if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE."
}

# Rename the installer for clarity
$installerName = "$($env:APP_NAME)-$($env:APP_VERSION)_64-bit"
if ($buildSuffix -ne 'release') {
    $installerName += "-$buildSuffix"
}
Rename-Item "build/jpackage/$($env:APP_NAME)-$($env:APP_VERSION).exe" "$installerName.exe" -ErrorAction Stop

# List tasks (for debugging)
tasklist

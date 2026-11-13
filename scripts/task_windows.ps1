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
} else {
    Write-Error "Error: $variablesPath not found."
    exit 1
}

Write-Output "Running exe packaging task on Windows..."

Write-Output "WIX_BINARIES_WINDOWS_URL: $env:WIX_BINARIES_WINDOWS_URL"

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
    Invoke-WebRequest -Uri $env:JDK_X64_WINDOWS_URL -OutFile jdk_x64_windows.zip
}

# WiX is needed by jpackage for the Windows installer.
Invoke-WebRequest -Uri $env:WIX_BINARIES_WINDOWS_URL -OutFile wix-binaries.zip

# List downloaded files
if (Test-Path jdk_x64_windows.zip) {
    Get-Item jdk_x64_windows.zip | Select-Object Name, Length
}
Get-Item wix-binaries.zip | Select-Object Name, Length

# Expand the downloaded archives
if (Test-Path jdk_x64_windows.zip) {
    Expand-Archive -Path jdk_x64_windows.zip -DestinationPath jdk_x64_windows
}
Expand-Archive -Path wix-binaries.zip -DestinationPath wix-binaries

# Set JAVA_HOME to the selected Java 25 installation.
if (-not $java25Available) {
    $VersionWithoutBuild = $env:JDK_WINDOWS_VERSION -split "\+" | Select-Object -First 1
    $javaHomeCandidate = "jdk_x64_windows/jdk-$VersionWithoutBuild"
}
$env:JAVA_HOME = $javaHomeCandidate
Write-Output "JAVA_HOME is set to: $env:JAVA_HOME"

# Copy WiX binaries into the JDK bin folder
Copy-Item -Path "wix-binaries/*" -Destination "$env:JAVA_HOME/bin" -Recurse

# Run some checks
& "$env:JAVA_HOME/bin/light.exe" -help
& "$env:JAVA_HOME/bin/candle.exe" -help
& "$env:JAVA_HOME/bin/java.exe" --version
& "$env:JAVA_HOME/bin/jlink.exe" --version
& "$env:JAVA_HOME/bin/jpackage.exe" --version

# Get the app version from Gradle
$env:APP_VERSION = (& .\gradlew.bat printVersion -q | Select-Object -Last 1).Trim()
Write-Output "App version is $env:APP_VERSION"

# Run jlink via Gradle
.\gradlew.bat jlink --stacktrace --info
.\gradlew.bat --stop

# Run jpackage to create the exe installer
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

# Rename the installer for clarity
Rename-Item "build/jpackage/$($env:APP_NAME)-$($env:APP_VERSION).exe" "build/jpackage/$($env:APP_NAME)-$($env:APP_VERSION)_64-bit.exe"

# List tasks (for debugging)
tasklist

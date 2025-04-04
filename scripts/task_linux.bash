#!/bin/bash
set -euo pipefail

# Determine the directory of this script.
SCRIPT_DIR="$(dirname "$0")"

# Install jq depending on the package manager available
if command -v apt-get &>/dev/null; then
  echo "Using apt-get to install jq..."
  apt-get update --quiet=2 && apt-get install --quiet=2 --yes jq
elif command -v microdnf &>/dev/null; then
  echo "Using microdnf to install jq..."
  microdnf update -y && microdnf install -y jq
else
  echo "Error: Neither apt-get nor microdnf found. Cannot install jq."
  exit 1
fi

# Load variables from the JSON file using jq
if [ -f "$SCRIPT_DIR/variables.json" ]; then
  export APP_NAME=$(jq -r '.APP_NAME' "$SCRIPT_DIR/variables.json")
  export LICENSE_FILE=$(jq -r '.LICENSE_FILE' "$SCRIPT_DIR/variables.json")
  export VENDOR=$(jq -r '.VENDOR' "$SCRIPT_DIR/variables.json")
  export MODULE_NAME_MAIN_CLASS=$(jq -r '.MODULE_NAME_MAIN_CLASS' "$SCRIPT_DIR/variables.json")
  export ICON_PATH_LINUX=$(jq -r '.ICON_PATH_LINUX' "$SCRIPT_DIR/variables.json")
else
  echo "Error: $SCRIPT_DIR/variables.json not found."
  exit 1
fi

# Usage: ./scripts/ci_task.sh <task>
# Valid tasks: jar, test, deb, rpm

TASK="$1"

if [ -z "$TASK" ]; then
  echo "Usage: $0 <task>"
  exit 1
fi

case "$TASK" in
  jar)
    echo "Running jar task..."
    echo "JAVA_HOME: ${JAVA_HOME}"
    ls -lh "${JAVA_HOME}"
    export APP_VERSION=$(./gradlew printVersion -q | tail -n 1)
    echo "App version is $APP_VERSION"
    ./gradlew build -x test --stacktrace --info
    ./gradlew --stop
    ;;
  test)
    echo "Running test task..."
    echo "JAVA_HOME: ${JAVA_HOME}"
    ls -lh "${JAVA_HOME}"
    apt-get install --quiet=2 --yes xvfb libxtst6 libxi6 openbox ffmpeg
    Xvfb :99 -screen 0 1280x1024x24 &
    export DISPLAY=:99
    openbox &
    ffmpeg -y -f x11grab -r 60 -i $DISPLAY -codec:v libvpx-vp9 -b:v 2M -pix_fmt yuv420p screen_capture.webm & echo $! > ffmpeg_pid
    ./gradlew test --stacktrace --info
    ./gradlew --stop
    sleep 1
    kill -SIGINT $(cat ffmpeg_pid)
    wait $(cat ffmpeg_pid) || true
    rm ffmpeg_pid
    ;;
  deb)
    echo "Running deb packaging task..."
    echo "JAVA_HOME: ${JAVA_HOME}"
    ls -lh "${JAVA_HOME}"
    apt-get install --quiet=2 --yes fakeroot binutils
    export APP_VERSION=$(./gradlew printVersion -q | tail -n 1)
    echo "App version is $APP_VERSION"
    ./gradlew jlink --stacktrace --info
    ./gradlew --stop
    jpackage \
      --verbose \
      --type deb \
      --resource-dir src/main/resources/jpackage \
      --runtime-image build/image \
      --module ${MODULE_NAME_MAIN_CLASS} \
      --java-options '-Dapp.dir=$APPDIR' \
      --dest build/jpackage \
      --name ${APP_NAME} \
      --app-version ${APP_VERSION} \
      --license-file ${LICENSE_FILE} \
      --vendor ${VENDOR} \
      --icon ${ICON_PATH_LINUX}
    ;;
  rpm)
    echo "Running rpm packaging task..."
    echo "JAVA_HOME: ${JAVA_HOME}"
    ls -lh "${JAVA_HOME}"
    microdnf install -y rpm-build
    export APP_VERSION=$(./gradlew printVersion -q | tail -n 1)
    echo "App version is $APP_VERSION"
    ./gradlew jlink --stacktrace --info
    ./gradlew --stop
    jpackage \
      --verbose \
      --type rpm \
      --resource-dir src/main/resources/jpackage \
      --runtime-image build/image \
      --module ${MODULE_NAME_MAIN_CLASS} \
      --java-options '-Dapp.dir=$APPDIR' \
      --dest build/jpackage \
      --name ${APP_NAME} \
      --app-version ${APP_VERSION} \
      --license-file ${LICENSE_FILE} \
      --vendor ${VENDOR} \
      --icon ${ICON_PATH_LINUX}
    ;;
  *)
    echo "Unknown task: $TASK"
    exit 1
    ;;
esac

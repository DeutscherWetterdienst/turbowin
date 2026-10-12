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
    apt-get install --quiet=2 --yes xvfb libxtst6 libxi6 openbox ffmpeg x11-utils

    # Preserve the test exit status and finalize the recording even when Gradle fails.
    xvfb_pid=""
    window_manager_pid=""
    recorder_pid=""
    cleanup_test() {
      test_exit=$?
      trap - EXIT
      set +e
      if [ -n "$recorder_pid" ]; then
        kill -INT "$recorder_pid" 2>/dev/null
        # A broken recorder must not keep the job alive during cleanup.
        if ! timeout 10s tail --pid="$recorder_pid" -f /dev/null; then
          kill -KILL "$recorder_pid" 2>/dev/null
        fi
        wait "$recorder_pid" 2>/dev/null
      fi
      if [ -n "$window_manager_pid" ]; then
        kill "$window_manager_pid" 2>/dev/null
        wait "$window_manager_pid" 2>/dev/null
      fi
      if [ -n "$xvfb_pid" ]; then
        kill "$xvfb_pid" 2>/dev/null
        wait "$xvfb_pid" 2>/dev/null
      fi
      exit "$test_exit"
    }
    trap cleanup_test EXIT
    trap 'exit 130' INT
    trap 'exit 143' TERM

    Xvfb :99 -screen 0 1280x1024x24 &
    xvfb_pid=$!
    export DISPLAY=:99
    # Wait for the X server before starting its clients.
    display_ready=false
    for ((attempt = 0; attempt < 50; attempt++)); do
      if xdpyinfo -display "$DISPLAY" >/dev/null 2>&1; then
        display_ready=true
        break
      fi
      kill -0 "$xvfb_pid"
      sleep 0.1
    done
    if [ "$display_ready" != true ]; then
      echo "Xvfb did not become ready."
      exit 1
    fi
    openbox &
    window_manager_pid=$!
    ffmpeg -y -f x11grab -r 60 -i "$DISPLAY" -codec:v libvpx-vp9 -b:v 2M -pix_fmt yuv420p screen_capture.webm &
    recorder_pid=$!
    timeout --kill-after=30s 10m ./gradlew test --no-daemon --stacktrace --info 2>&1 | tee test-run.log
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

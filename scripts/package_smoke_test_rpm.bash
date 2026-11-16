#!/bin/bash
set -Eeuo pipefail

artifact=${1:?usage: $0 path/to/TurboWin-*.rpm [diagnostic-directory]}
diag_dir=${2:-build/package-smoke/rpm}
mkdir -p "$diag_dir"
artifact=$(readlink -f "$artifact")

package_name=$(rpm -qp --qf '%{NAME}' "$artifact")
package_version=$(rpm -qp --qf '%{VERSION}-%{RELEASE}' "$artifact")
rpm -qpi "$artifact" >"$diag_dir/package-info.log" 2>&1
rpm -qpl "$artifact" >"$diag_dir/package-contents.log" 2>&1
rpm -qp --scripts "$artifact" >"$diag_dir/package-scriptlets.log" 2>&1

launcher=/opt/turbowin/bin/TurboWin
app_dir=/opt/turbowin
if command -v dnf >/dev/null 2>&1; then
  package_manager=dnf
elif command -v microdnf >/dev/null 2>&1; then
  package_manager=microdnf
else
  package_manager=rpm
fi

# Minimal CI images may not contain the freedesktop menu directories required by the
# jpackage-generated xdg-desktop-menu scriptlets.
install -d -m 755 /usr/share/applications /usr/share/desktop-directories \
  /etc/xdg/menus/applications-merged

log_run() {
  local name=$1
  shift
  echo "== $name ==" | tee -a "$diag_dir/operations.log"
  "$@" > >(tee -a "$diag_dir/$name.log") 2>&1
}

package_install() {
  if [[ "$package_manager" = rpm ]]; then
    rpm -Uvh --nodeps "$1"
  else
    "$package_manager" install --assumeyes "$1"
  fi
}

package_remove() {
  if [[ "$package_manager" = rpm ]]; then
    rpm -e "$package_name"
  else
    "$package_manager" remove --assumeyes "$package_name"
  fi
}

assert_installed() {
  rpm -q "$package_name" >/dev/null
  test -x "$launcher"
  test -d "$app_dir"
  test -x "$app_dir/bin/TurboWin"
  test -x "$app_dir/bin/TurboWin.run"
  test -f "$app_dir/lib/app/TurboWin.run.cfg"
  rpm -ql "$package_name" >"$diag_dir/installed-files.log"
}

smoke_launch() {
  local name=$1
  set +e
  timeout --signal=TERM --kill-after=10s 20s \
    xvfb-run -a -s '-screen 0 1280x1024x24' "$launcher" \
    >"$diag_dir/$name.log" 2>&1
  local status=$?
  set -e
  if [[ $status -ne 0 && $status -ne 124 && $status -ne 137 && $status -ne 143 ]]; then
    echo "Application startup failed with exit status $status" >&2
    return "$status"
  fi
}

cleanup() {
  status=$?
  set +e
  pkill -f "$launcher" >/dev/null 2>&1 || true
  rpm -q "$package_name" >/dev/null 2>&1 && package_remove >>"$diag_dir/cleanup.log" 2>&1 || true
  rm -rf "$app_dir" "$launcher"
  exit "$status"
}
trap cleanup EXIT

log_run install package_install "$artifact"
assert_installed
smoke_launch launch-after-install

log_run remove package_remove
if rpm -q "$package_name" >/dev/null 2>&1; then
  echo "Package remained installed after dnf remove" >&2
  exit 1
fi
test ! -e "$launcher"

log_run reinstall package_install "$artifact"
assert_installed
# The data directory is created by postinst but is not package-owned. Removal must not assume it
# still exists after a user has deleted their optional application data.
rm -rf "$app_dir/data"
log_run remove-with-missing-files package_remove
if rpm -q "$package_name" >/dev/null 2>&1; then
  echo "Package remained installed after missing-file removal" >&2
  exit 1
fi
test ! -e "$app_dir/bin"
test ! -e "$app_dir/lib"
# /opt/turbowin/data is deliberately created as user data by postinst and is not package-owned.
rm -rf "$app_dir/data"
rmdir "$app_dir" 2>/dev/null || true
test ! -e "$launcher"

if [[ -n "${TURBOWIN_OLDER_PACKAGE:-}" ]]; then
  log_run install-older package_install "$(readlink -f "$TURBOWIN_OLDER_PACKAGE")"
  log_run upgrade package_install "$artifact"
  assert_installed
  log_run remove-after-upgrade package_remove
fi

echo "RPM lifecycle smoke test passed for $package_name $package_version"

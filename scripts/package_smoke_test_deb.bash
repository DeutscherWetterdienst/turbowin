#!/bin/bash
set -Eeuo pipefail

artifact=${1:?usage: $0 path/to/TurboWin-*.deb [diagnostic-directory]}
diag_dir=${2:-build/package-smoke/deb}
mkdir -p "$diag_dir"
artifact=$(readlink -f "$artifact")

package_name=$(dpkg-deb -f "$artifact" Package)
package_version=$(dpkg-deb -f "$artifact" Version)
control_dir="$diag_dir/control"
mkdir -p "$control_dir"
dpkg-deb --control "$artifact" "$control_dir"
dpkg-deb --info "$artifact" >"$diag_dir/package-info.log" 2>&1
dpkg-deb --contents "$artifact" >"$diag_dir/package-contents.log" 2>&1

launcher=/opt/turbowin/bin/TurboWin
app_dir=/opt/turbowin

# Minimal CI images may not contain the freedesktop menu directories required by the
# jpackage-generated xdg-desktop-menu maintainer commands.
install -d -m 755 /usr/share/applications /usr/share/desktop-directories \
  /etc/xdg/menus/applications-merged

log_run() {
  local name=$1
  shift
  echo "== $name ==" | tee -a "$diag_dir/operations.log"
  "$@" > >(tee -a "$diag_dir/$name.log") 2>&1
}

assert_installed() {
  dpkg-query -W -f='${Status}' "$package_name" | grep -Fxq 'install ok installed'
  test -x "$launcher"
  test -d "$app_dir"
  test -x "$launcher"
  test -x "$app_dir/bin/TurboWin.run"
  test -f "$app_dir/lib/app/TurboWin.run.cfg"
  test -f "$app_dir/lib/runtime/OSM/leaflet.js"
  test -f "$app_dir/lib/runtime/OSM/OSMPublicTransport/2/0/0.png"
  dpkg -L "$package_name" >"$diag_dir/installed-files.log"
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
  if dpkg-query -W -f='${Status}' "$package_name" >/dev/null 2>&1; then
    dpkg --configure -a >>"$diag_dir/cleanup.log" 2>&1 || true
    apt-get purge --yes "$package_name" >>"$diag_dir/cleanup.log" 2>&1 || true
  fi
  rm -rf "$app_dir" "$launcher"
  exit "$status"
}
trap cleanup EXIT

log_run install apt-get install --yes "$artifact"
assert_installed
smoke_launch launch-after-install

log_run remove apt-get remove --yes "$package_name"
if dpkg-query -W -f='${Status}' "$package_name" 2>/dev/null | grep -q 'install ok installed'; then
  echo "Package remained installed after apt-get remove" >&2
  exit 1
fi
  test ! -e "$launcher"

log_run reinstall apt-get install --yes "$artifact"
assert_installed
# The data directory is created by postinst but is not package-owned. Removal must not assume it
# still exists after a user has deleted their optional application data.
rm -rf "$app_dir/data"
log_run purge-with-missing-files apt-get purge --yes "$package_name"
if dpkg-query -W -f='${Status}' "$package_name" 2>/dev/null | grep -qE 'installed|config-files'; then
  echo "Package was not completely purged" >&2
  exit 1
fi
test ! -e "$app_dir/bin"
test ! -e "$app_dir/lib"
# /opt/turbowin/data is deliberately created as user data by postinst and is not package-owned.
rm -rf "$app_dir/data"
rmdir "$app_dir" 2>/dev/null || true
test ! -e "$launcher"

if [[ -n "${TURBOWIN_OLDER_PACKAGE:-}" ]]; then
  log_run install-older apt-get install --yes "$(readlink -f "$TURBOWIN_OLDER_PACKAGE")"
  log_run upgrade apt-get install --yes "$artifact"
  assert_installed
  log_run remove-after-upgrade apt-get purge --yes "$package_name"
fi

echo "DEB lifecycle smoke test passed for $package_name $package_version"

#!/usr/bin/env bash
set -euo pipefail

# Exercise the real Gradle build in an isolated repository without modifying the working checkout.
project_root=$(cd "$(dirname "$0")/.." && pwd)
test_dir=$(mktemp -d)
trap 'rm -rf -- "$test_dir"' EXIT
mkdir "$test_dir/repo" "$test_dir/archive"
cp "$project_root/build.gradle" "$project_root/settings.gradle" "$project_root/.gitignore" "$test_dir/repo/"
cp "$project_root/build.gradle" "$project_root/settings.gradle" "$project_root/.gitignore" "$test_dir/archive/"
git -C "$test_dir/repo" init -q
git -C "$test_dir/repo" add build.gradle settings.gradle .gitignore
git -C "$test_dir/repo" -c user.name='Test' -c user.email='test@example.invalid' commit -qm initial
test_commit=$(git -C "$test_dir/repo" rev-parse HEAD)
short_commit=${test_commit:0:7}

run_gradle() {
  env -u GITHUB_SHA -u GITHUB_REF_TYPE -u GITHUB_REF_NAME -u CI_COMMIT_SHA -u CI_COMMIT_TAG \
    -u GITHUB_ACTIONS -u GITLAB_CI \
    "$project_root/gradlew" -p "$1" "${@:2}" --quiet --console=plain
}

[[ $(run_gradle "$test_dir/repo" printBuildArtifactSuffix) == "dev-$short_commit" ]]
run_gradle "$test_dir/repo" generateAppInfoJava
generated="$test_dir/repo/build/generated/source/appinfo/java/TurboWinAppInfo.java"
grep -Fq "6.0.0-dev+$short_commit" "$generated"
grep -Fq "$test_commit" "$generated"

git -C "$test_dir/repo" tag v6.0.0
[[ $(run_gradle "$test_dir/repo" printBuildArtifactSuffix) == release ]]
run_gradle "$test_dir/repo" generateAppInfoJava
grep -Fq 'BUILD_VERSION = "6.0.0"' "$generated"

mkdir "$test_dir/repo/untracked-change"
cp "$project_root/.gitignore" "$test_dir/repo/untracked-change/file"
[[ $(run_gradle "$test_dir/repo" printBuildArtifactSuffix) == "dev-$short_commit-dirty" ]]
[[ $(run_gradle "$test_dir/archive" printBuildArtifactSuffix) == dev-unknown ]]

# Shallow CI checkouts may not contain tag objects; the CI tag identifies the checkout.
git -C "$test_dir/repo" tag -d v6.0.0 >/dev/null
rm "$test_dir/repo/untracked-change/file"
rmdir "$test_dir/repo/untracked-change"
ci_suffix=$(env -u GITHUB_SHA -u GITHUB_REF_TYPE -u GITHUB_REF_NAME \
  CI_COMMIT_SHA="$test_commit" CI_COMMIT_TAG=v6.0.0 \
  "$project_root/gradlew" -p "$test_dir/repo" printBuildArtifactSuffix --quiet --console=plain)
[[ "$ci_suffix" == release ]]
github_suffix=$(env -u CI_COMMIT_SHA -u CI_COMMIT_TAG \
  GITHUB_SHA="$test_commit" GITHUB_REF_TYPE=tag GITHUB_REF_NAME=v6.0.0 \
  "$project_root/gradlew" -p "$test_dir/repo" printBuildArtifactSuffix --quiet --console=plain)
[[ "$github_suffix" == release ]]
archive_suffix=$(env -u CI_COMMIT_SHA -u CI_COMMIT_TAG \
  GITHUB_ACTIONS=true GITHUB_SHA="$test_commit" GITHUB_REF_TYPE=branch GITHUB_REF_NAME=main \
  "$project_root/gradlew" -p "$test_dir/archive" printBuildArtifactSuffix --quiet --console=plain)
[[ "$archive_suffix" == "dev-$short_commit" ]]
archive_release=$(env -u CI_COMMIT_SHA -u CI_COMMIT_TAG \
  GITHUB_ACTIONS=true GITHUB_SHA="$test_commit" GITHUB_REF_TYPE=tag GITHUB_REF_NAME=v6.0.0 \
  "$project_root/gradlew" -p "$test_dir/archive" printBuildArtifactSuffix --quiet --console=plain)
[[ "$archive_release" == release ]]
echo 'Build identity checks passed.'

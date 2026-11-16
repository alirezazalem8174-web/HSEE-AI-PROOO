#!/usr/bin/env bash
set -euo pipefail
: "${ANDROID_SDK_ROOT:=${ANDROID_HOME:-}}"
if [[ -z "${ANDROID_SDK_ROOT}" ]]; then
  echo "ANDROID_SDK_ROOT/ANDROID_HOME is not set." >&2
  exit 2
fi
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle 8.7 is required. Install it or use the included GitHub Actions workflow." >&2
  exit 2
fi
export PATH="$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/build-tools/35.0.0:$PATH"
gradle --no-daemon --stacktrace :app:assembleDebug
APK="app/build/outputs/apk/debug/app-debug.apk"
test -s "$APK"
sha256sum "$APK"
echo "APK: $APK"

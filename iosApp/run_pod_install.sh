#!/bin/bash

# Устанавливаем pods
pod install

# Удаляем OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED из xcconfig (даём Gradle собрать KMP)
for xcconfig in "Pods/Target Support Files/composeApp/composeApp.debug.xcconfig" "Pods/Target Support Files/composeApp/composeApp.release.xcconfig"; do
  if [ -f "$xcconfig" ]; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
      sed -i '' '/^OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED\b/d' "$xcconfig"
    else
      sed -i '/^OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED\b/d' "$xcconfig"
    fi
    echo "Removed OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED from $xcconfig"
  fi
done

echo "Pod install complete. OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED is not set (Gradle build enabled)."


#!/bin/bash

# Устанавливаем pods
pod install

# Добавляем OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED в xcconfig файлы для composeApp
for xcconfig in "Pods/Target Support Files/composeApp/composeApp.debug.xcconfig" "Pods/Target Support Files/composeApp/composeApp.release.xcconfig"; do
  if [ -f "$xcconfig" ]; then
    if ! grep -q "OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" "$xcconfig"; then
      # Добавляем переменную после строки 3 (ENABLE_USER_SCRIPT_SANDBOXING = NO)
      if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS использует BSD sed
        sed -i '' '3 a\
OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED = YES
' "$xcconfig"
      else
        # Linux использует GNU sed
        sed -i '3 a\OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED = YES\n' "$xcconfig"
      fi
      echo "Added OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED to $xcconfig"
    fi
  fi
done

echo "Pod install complete. OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED has been set."


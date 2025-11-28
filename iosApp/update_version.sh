#!/bin/bash

# Скрипт для обновления версий iOS из Gradle
# Этот скрипт должен быть добавлен как Build Phase в Xcode
# Добавьте его как "Run Script" phase перед "Compile Sources"

# Переходим в корневую директорию проекта
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

# Обновляем версии в Config.xcconfig
./gradlew :composeApp:updateIOSVersion --no-configuration-cache --quiet



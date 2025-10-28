# iOS Build Instructions

## Требования

- macOS 13.0 или выше
- Xcode 14.0 или выше
- CocoaPods 1.11.0 или выше
- Git

## Установка зависимостей

### 1. Установка CocoaPods

Если у вас еще не установлен CocoaPods:

```bash
sudo gem install cocoapods
```

### 2. Установка pods для проекта

Используйте специальный скрипт для установки зависимостей:

```bash
./run_pod_install.sh
```

Этот скрипт автоматически:
- Устанавливает CocoaPods зависимости
- Добавляет переменную `OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED = YES` в конфигурационные файлы

⚠️ **Важно**: Используйте `./run_pod_install.sh` вместо обычного `pod install`. Это необходимо для корректной работы Kotlin Multiplatform фреймворков.

## Сборка проекта

### Через Xcode (рекомендуется)

1. Откройте workspace:
   ```bash
   open iosApp.xcworkspace
   ```
   
   ⚠️ **Важно**: Открывайте именно `.xcworkspace`, а не `.xcodeproj`!

2. Выберите симулятор или подключенное устройство из списка

3. Нажмите `Cmd + R` для запуска приложения

### Через командную строку

```bash
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -destination 'id=YOUR_DEVICE_ID'
```

Для симулятора:
```bash
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -destination 'platform=iOS Simulator,name=iPhone 15 Pro'
```

## Почему нужен специальный скрипт?

Kotlin Multiplatform использует Gradle для сборки фреймворков. Переменная `OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED = YES` указывает Xcode использовать предсобранные фреймворки из `composeApp/build/cocoapods/framework/` вместо запуска Gradle задач напрямую.

Это решает проблему, когда:
- Gradle task `syncFramework` не может корректно определить архитектуру устройства
- Xcode пытается запустить Gradle сборку для каждой архитектуры отдельно

## Общие проблемы и решения

### Ошибка: "Command PhaseScriptExecution failed with a nonzero exit code"

Если сборка падает с этой ошибкой, убедитесь что:
1. Переменная `OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED = YES` установлена в `Pods/Target Support Files/composeApp/composeApp.debug.xcconfig`
2. Вы использовали `./run_pod_install.sh` вместо обычного `pod install`

### Ошибка: "ld: framework 'YandexMapsMobile' not found"

Эта ошибка обычно возникает при неправильной настройке путей к фреймворкам. Убедитесь что:
1. Фреймворки собраны через Gradle: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`
2. В `composeApp/build.gradle.kts` корректно указаны пути к `YandexMapsMobile.xcframework`

### Фреймворки не собираются

Предварительно соберите фреймворки через Gradle:

```bash
# Для симулятора (arm64)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Для устройства
./gradlew :composeApp:linkDebugFrameworkIosArm64
```

## Дополнительная информация

- **Минимальная версия iOS**: 16.0
- **Целевая версия Xcode**: 14.0+
- **Architecture**: arm64 (M-series Macs) и x86_64 (Intel Macs)

## Полезные команды

### Очистка кеша

Если возникают проблемы со сборкой:

```bash
# Очистка кеша Pods
rm -rf Pods/
rm -rf ~/Library/Developer/Xcode/DerivedData/iosApp-*

# Переустановка pods
./run_pod_install.sh
```

### Просмотр установленных pods

```bash
pod list
```

### Обновление pods

```bash
./run_pod_install.sh
```


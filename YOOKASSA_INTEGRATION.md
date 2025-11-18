# Интеграция YooKassaPayments через Swift Package Manager

## Шаги для подключения

### 1. Добавьте YooKassaPayments через SPM в Xcode

~~1. Откройте `iosApp.xcworkspace` в Xcode
2. File → Add Package Dependencies...
3. Введите URL: `https://git.yoomoney.ru/scm/sdk/yookassa-payments-swift.git`
4. Выберите версию: `8.1.1` (или нужный тег)
5. Выберите таргет `iosApp`
6. Нажмите "Add Package"~~

### 2. Добавьте Swift обертку в iOS проект

1. В Xcode добавьте файл `YooKassaWrapper.swift` в проект `iosApp`
   - Файл уже создан в `iosApp/YooKassaWrapper.swift`
   - Перетащите его в Xcode проект или добавьте через "Add Files to iosApp"
   - Убедитесь, что файл добавлен в таргет `iosApp`

2. Реализуйте методы в `YooKassaWrapper.swift` согласно документации YooKassaPayments SDK

3. **Важно**: Для доступа к Swift обертке из Kotlin нужно:
   - Убедиться, что класс помечен как `@objc public class`
   - Методы должны быть помечены как `@objc public func`
   - Для вызова из Kotlin используйте platform.* API или настройте cinterop

### 3. Обновите actual реализацию в Kotlin

После создания Swift обертки обновите `YooKassaPaymentService.ios.kt` для вызова Swift кода.
Можно использовать один из подходов:
- **Platform.* API** - для прямого вызова
- **Cinterop** - для генерации Kotlin bindings из Objective-C интерфейса
- **Expect/Actual с Swift bridge** - через специальный механизм

### 4. Использование из Kotlin

```kotlin
// В commonMain или iosMain
val paymentService = YooKassaPaymentService()

// Инициализация платежа
val result = paymentService.initializePayment(
    amount = 1000.0,
    currency = "RUB",
    description = "Оплата заказа"
)

if (result.success) {
    // Обработка успешного результата
    val paymentId = result.paymentId
} else {
    // Обработка ошибки
    val error = result.error
}
```

## Структура файлов

- `commonMain/.../YooKassaPaymentService.kt` - expect интерфейс
- `iosMain/.../YooKassaPaymentService.ios.kt` - actual реализация для iOS
- `iosApp/YooKassaWrapper.swift` - Swift обертка для доступа к SDK

## Примечания

- YooKassaPayments SDK доступен только на iOS
- Для Android используйте `yookassa-android-sdk` (уже подключен)
- Swift обертка нужна для bridge между Kotlin и Swift SDK


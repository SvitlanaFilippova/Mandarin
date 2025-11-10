package com.mandarinkafe.mandarin.features.auth.data.sms

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * iOS реализация автоматического получения SMS-кодов
 *
 * На iOS автозаполнение SMS-кодов работает автоматически через систему,
 * когда SMS содержит правильный формат.
 *
 * Требования к SMS для автозаполнения на iOS:
 * 1. SMS должна содержать 6-значный код
 * 2. РЕКОМЕНДУЕТСЯ указать домен приложения в формате:
 *    - "Ваш код для mandarinkafe.com: 123456" или
 *    - "@mandarinkafe.com #123456"
 * 3. (Опционально) В Info.plist добавить Associated Domains:
 *    com.apple.developer.associated-domains с webcredentials:mandarinkafe.com
 *
 * Compose Multiplatform автоматически обрабатывает автозаполнение через
 * KeyboardType.Number в OtpTextField, поэтому здесь просто возвращаем пустой Flow.
 * Система iOS сама предложит код из SMS в поле ввода.
 */
actual fun getSmsRetriever(): SmsRetriever = object : SmsRetriever {
    override fun startListening(): Flow<String?> {
        // На iOS автозаполнение работает нативно через систему
        // Compose автоматически поддерживает это через TextField с правильным KeyboardType
        // Поэтому здесь мы просто возвращаем пустой Flow
        return flowOf(null)
    }

    override fun stopListening() {
        // На iOS нет необходимости явно останавливать прослушивание
    }
}


package com.mandarinkafe.mandarin.features.auth.data.sms

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * iOS реализация автоматического получения SMS-кодов
 *
 * На iOS автозаполнение SMS-кодов работает автоматически через систему,
 * если SMS содержит правильный формат с доменом приложения.
 *
 * Для работы автозаполнения на iOS нужно:
 * 1. SMS должна содержать код в формате: "Your code is 123456" или просто "123456"
 * 2. SMS должна содержать домен вашего приложения в формате: @yourdomain.com #123456
 * 3. В Associated Domains в Xcode добавить webcredentials:yourdomain.com
 *
 * Compose для iOS автоматически поддерживает textContentType(.oneTimeCode),
 * что позволяет системе автоматически предлагать коды из SMS.
 */
class IosSmsRetriever : SmsRetriever {

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

actual fun getSmsRetriever(): SmsRetriever {
    return IosSmsRetriever()
}





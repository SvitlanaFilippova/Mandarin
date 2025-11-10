package com.mandarinkafe.mandarin.features.auth.data.sms

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для автоматического получения SMS-кодов на разных платформах
 */
interface SmsRetriever {
    /**
     * Начать прослушивание входящих SMS с кодами верификации
     * @return Flow, который эмитит полученный код
     */
    fun startListening(): Flow<String?>

    /**
     * Остановить прослушивание SMS
     */
    fun stopListening()
}

/**
 * Expect функция для получения платформенно-специфичной реализации SmsRetriever
 */
expect fun getSmsRetriever(): SmsRetriever











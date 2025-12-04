package com.mandarinkafe.mandarin.features.auth.util

/**
 * Expect функция для получения хэша подписи приложения для SMS Retriever API
 * 
 * Возвращает 11-символьный хэш в Base64 формате для Android, null для iOS
 * - Если установлено через Google Play с App Signing → хэш от Google Play ключа
 * - Если установлено напрямую (APK) → хэш от локального keystore
 */
expect fun getAppSignatureHashForSms(): String?


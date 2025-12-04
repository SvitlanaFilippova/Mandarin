package com.mandarinkafe.mandarin.features.auth.util

import android.app.Application
import android.content.Context
import io.github.aakira.napier.Napier
import org.koin.mp.KoinPlatform.getKoin

/**
 * Получает Android Application context через Koin
 * Если Koin не инициализирован, использует fallback через рефлексию
 */
private fun getAndroidContext(): Context {
    return try {
        // Пытаемся получить через Koin (предпочтительный способ)
        val koin = getKoin()
        koin.get<Application>()
    } catch (e: Exception) {
        Napier.w("getAndroidContext: fallback на рефлекексию", e)
        // Fallback: используем рефлексию только если Koin не инициализирован
        // Это может произойти, если функция вызвана до инициализации Koin
        try {
            @Suppress("DEPRECATION", "UnsafeCallOnNullableType")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
            currentApplicationMethod.invoke(null) as? Context
                ?: throw IllegalStateException("Unable to get Android context")
        } catch (reflectionException: Exception) {
            throw IllegalStateException(
                "Unable to get Android context: Koin not initialized and reflection failed. " +
                        "Make sure Koin is initialized before calling getAppSignatureHashForSms()",
                reflectionException
            )
        }
    }
}

/**
 * Android реализация получения хэша подписи приложения
 */
actual fun getAppSignatureHashForSms(): String? {
    return try {
        val context = getAndroidContext()
        AppSignatureHashHelper.getAppSignatureHash(context)
    } catch (e: Exception) {
        null
    }
}


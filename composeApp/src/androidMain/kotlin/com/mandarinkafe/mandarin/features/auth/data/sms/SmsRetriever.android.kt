package com.mandarinkafe.mandarin.features.auth.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.android.gms.auth.api.phone.SmsRetriever as GoogleSmsRetriever

/**
 * Извлекает 6-значный код из текста SMS
 */
private fun extractCodeFromMessage(message: String): String? {
    // Ищем 6-значную последовательность цифр
    val pattern = "\\b\\d{6}\\b".toRegex()
    return pattern.find(message)?.value
}

/**
 * Получает Android Application context через Koin
 * Если Koin не инициализирован, использует fallback через рефлексию
 */
private fun getAndroidContext(): Context {
    return try {
        // Пытаемся получить через Koin (предпочтительный способ)
        val koin = org.koin.mp.KoinPlatform.getKoin()
        koin.get<android.app.Application>()
    } catch (e: Exception) {
        // Fallback: используем рефлексию только если Koin не инициализирован
        // Это может произойти, если функция вызвана до инициализации Koin
        // ВАЖНО: Это временное решение, рефлексия может не работать на новых версиях Android
        try {
            @Suppress("DEPRECATION", "UnsafeCallOnNullableType")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
            currentApplicationMethod.invoke(null) as? Context
                ?: throw IllegalStateException("Unable to get Android context")
        } catch (reflectionException: Exception) {
            throw IllegalStateException(
                "Unable to get Android context for SmsRetriever: Koin not initialized and reflection failed. " +
                        "Make sure Koin is initialized before using SmsRetriever",
                reflectionException
            )
        }
    }
}

/**
 * Извлекает статус из Intent extras с учетом версии API
 */
private fun Intent.getStatusExtra(): Status? {
    val extras = this.extras ?: return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        extras.getParcelable(GoogleSmsRetriever.EXTRA_STATUS, Status::class.java)
    } else {
        @Suppress("DEPRECATION")
        extras.getParcelable(GoogleSmsRetriever.EXTRA_STATUS)
    }
}

/**
 * Регистрирует BroadcastReceiver с правильными флагами в зависимости от версии API
 */
private fun Context.registerSmsReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
    } else {
        @Suppress("UnspecifiedRegisterReceiverFlag")
        registerReceiver(receiver, filter)
    }
}

/**
 * Создает BroadcastReceiver для обработки SMS
 */
private fun createSmsReceiver(
    onCodeReceived: (String?) -> Unit,
): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (GoogleSmsRetriever.SMS_RETRIEVED_ACTION != intent.action) return

            val status = intent.getStatusExtra()
            Napier.i { "SMS Retriever: получен Intent, статус: ${status?.statusCode}" }
            
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = intent.extras?.getString(GoogleSmsRetriever.EXTRA_SMS_MESSAGE)
                    Napier.i { "SMS Retriever: получено SMS сообщение: $message" }
                    val code = message?.let { extractCodeFromMessage(it) }
                    if (code == null) {
                        Napier.w { "SMS Retriever: не удалось извлечь код из сообщения: $message" }
                    }
                    onCodeReceived(code)
                }

                CommonStatusCodes.TIMEOUT -> {
                    Napier.w { "SMS Retriever: timeout - SMS не получена в течение 5 минут" }
                    onCodeReceived(null)
                }
                
                else -> {
                    Napier.e { "SMS Retriever: неизвестный статус: ${status?.statusCode}, сообщение: ${status?.statusMessage}" }
                    onCodeReceived(null)
                }
            }
        }
    }
}

/**
 * Android реализация автоматического получения SMS-кодов
 * Использует Google SMS Retriever API
 *
 * ВАЖНО: SMS должна содержать 11-символьный хеш приложения в конце!
 * Формат SMS: "<#> Your code is 123456\n\nFA+9qCX9VSu"
 * Где FA+9qCX9VSu - хеш приложения
 */
actual fun getSmsRetriever(): SmsRetriever {
    val context = getAndroidContext()

    return object : SmsRetriever {
        private var smsReceiver: BroadcastReceiver? = null

        override fun startListening(): Flow<String?> = callbackFlow {
            val client = GoogleSmsRetriever.getClient(context)
            val task = client.startSmsRetriever()

            task.addOnSuccessListener {
                Napier.i { "SMS Retriever: успешно запущен" }
                val receiver = createSmsReceiver { code -> 
                    if (code != null) {
                        Napier.i { "SMS Retriever: получен код: $code" }
                    } else {
                        Napier.w { "SMS Retriever: код не получен (timeout или ошибка)" }
                    }
                    trySend(code) 
                }
                smsReceiver = receiver
                val intentFilter = IntentFilter(GoogleSmsRetriever.SMS_RETRIEVED_ACTION)
                context.registerSmsReceiver(receiver, intentFilter)
                Napier.i { "SMS Retriever: BroadcastReceiver зарегистрирован" }
            }

            task.addOnFailureListener { exception ->
                Napier.e(exception) { "SMS Retriever: ошибка при запуске - ${exception.message}" }
                trySend(null)
            }

            awaitClose {
                stopListening()
            }
        }

        override fun stopListening() {
            smsReceiver?.let {
                try {
                    context.unregisterReceiver(it)
                } catch (e: Exception) {
                    Napier.e { "SMS retriever, stopListening: $e" }
                    // Receiver уже отписан
                }
                smsReceiver = null
            }
        }
    }
}


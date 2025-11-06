package com.mandarinkafe.mandarin.features.auth.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.android.gms.auth.api.phone.SmsRetriever as GoogleSmsRetriever

/**
 * Android реализация автоматического получения SMS-кодов
 * Использует Google SMS Retriever API
 *
 * ВАЖНО: SMS должна содержать 11-символьный хеш приложения в конце!
 * Формат SMS: "<#> Your code is 123456\n\nFA+9qCX9VSu"
 * Где FA+9qCX9VSu - хеш приложения (см. логи при запуске)
 */
class AndroidSmsRetriever(private val context: Context) : SmsRetriever {

    private var smsReceiver: BroadcastReceiver? = null

    override fun startListening(): Flow<String?> = callbackFlow {
        val client = GoogleSmsRetriever.getClient(context)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            // SMS Retriever запущен успешно
            smsReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (GoogleSmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                        val extras = intent.extras
                        // Используем правильный метод в зависимости от API уровня
                        val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras?.getParcelable(
                                GoogleSmsRetriever.EXTRA_STATUS,
                                Status::class.java
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            extras?.getParcelable(GoogleSmsRetriever.EXTRA_STATUS)
                        }

                        when (status?.statusCode) {
                            CommonStatusCodes.SUCCESS -> {
                                // Получить текст SMS
                                val message =
                                    extras?.getString(GoogleSmsRetriever.EXTRA_SMS_MESSAGE)
                                message?.let {
                                    // Извлечь код из SMS (предполагаем 6-значный код)
                                    val code = extractCodeFromMessage(it)
                                    trySend(code)
                                }
                            }

                            CommonStatusCodes.TIMEOUT -> {
                                // Тайм-аут ожидания SMS
                                trySend(null)
                            }
                        }
                    }
                }
            }

            val intentFilter = IntentFilter(GoogleSmsRetriever.SMS_RETRIEVED_ACTION)
            // Регистрируем receiver с правильными флагами
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+ требует явно указать RECEIVER_EXPORTED
                context.registerReceiver(smsReceiver, intentFilter, Context.RECEIVER_EXPORTED)
            } else {
                // API < 33: используем старый метод
                // SMS_RETRIEVED_ACTION - это защищенный broadcast от Google Play Services,
                // поэтому безопасно регистрировать без флага
                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.registerReceiver(smsReceiver, intentFilter)
            }
        }

        task.addOnFailureListener {
            // Не удалось запустить SMS Retriever
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
                // Receiver уже отписан
            }
            smsReceiver = null
        }
    }

    /**
     * Извлекает 6-значный код из текста SMS
     */
    private fun extractCodeFromMessage(message: String): String? {
        // Ищем 6-значную последовательность цифр
        val pattern = "\\b\\d{6}\\b".toRegex()
        return pattern.find(message)?.value
    }
}

actual fun getSmsRetriever(): SmsRetriever {
    // Получаем контекст из Android Application
    val context = try {
        // Попытка получить контекст через рефлексию
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
        currentApplicationMethod.invoke(null) as Context
    } catch (e: Exception) {
        throw IllegalStateException("Unable to get Android context for SmsRetriever", e)
    }

    return AndroidSmsRetriever(context)
}


package com.mandarinkafe.mandarin.features.payment

import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import kotlin.coroutines.resume

/**
 * iOS реализация YooKassaPaymentService через "умный платёж" (Smart Payment)
 * 
 * Для iOS не используем SDK YooKassaPayments, а работаем через API:
 * 1. initializePayment - возвращает success, но paymentToken = null
 *    (сервер создаст платеж без payment_token через API YooKassa)
 * 2. openPaymentUrl - открывает confirmation_url в браузере/Safari
 */
actual class YooKassaPaymentService {

    actual suspend fun initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String
    ): PaymentResult {
        // Для iOS "умного платежа" не нужен payment_token от SDK
        // Сервер создаст платеж напрямую через API YooKassa
        return PaymentResult(
            success = true,
            paymentToken = null, // Для iOS сервер создаст платеж без payment_token
            error = null
        )
    }

    actual suspend fun openPaymentUrl(confirmationUrl: String): PaymentResult {
        return try {
            val url = NSURL(string = confirmationUrl)
            if (url != null) {
                val canOpen = UIApplication.sharedApplication.canOpenURL(url)
                if (canOpen) {
                    // Используем новый API с completion handler
                    return suspendCancellableCoroutine { continuation ->
                        UIApplication.sharedApplication.openURL(url, mapOf<Any?, Any>()) { success ->
                            if (success) {
                                continuation.resume(
                                    PaymentResult(
                                        success = true,
                                        paymentToken = null,
                                        error = null
                                    )
                                )
                            } else {
                                Napier.e("[YooKassa iOS] Не удалось открыть URL: $confirmationUrl")
                                continuation.resume(
                                    PaymentResult(
                                        success = false,
                                        paymentToken = null,
                                        error = "Не удалось открыть URL для оплаты"
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Napier.e("[YooKassa iOS] URL не может быть открыт: $confirmationUrl")
                    PaymentResult(
                        success = false,
                        paymentToken = null,
                        error = "Не удалось открыть URL для оплаты"
                    )
                }
            } else {
                Napier.e("[YooKassa iOS] Некорректный URL: $confirmationUrl")
                PaymentResult(
                    success = false,
                    paymentToken = null,
                    error = "Некорректный URL для оплаты"
                )
            }
        } catch (e: Throwable) {
            Napier.e("[YooKassa iOS] Ошибка при открытии URL", e)
            PaymentResult(
                success = false,
                paymentToken = null,
                error = "Ошибка при открытии URL: ${e.message}"
            )
        }
    }
}

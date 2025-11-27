package com.mandarinkafe.mandarin.features.payment

import android.content.Intent
import androidx.core.net.toUri
import com.mandarinkafe.mandarin.shared.BuildKonfig

actual class YooKassaPaymentService {

    actual suspend fun initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
        orderId: String,
    ): PaymentResult {
        val clientApplicationKey = BuildKonfig.YOOKASSA_CLIENT_APPLICATION_KEY
        val shopId = BuildKonfig.YOOKASSA_SHOP_ID

        return try {
            YooKassaActivityHelper.initializePayment(
                amount = amount,
                subtitle = subtitle,
                clientApplicationKey = clientApplicationKey,
                shopId = shopId,
                userPhone = userPhone,
                orderId = orderId
            )
        } catch (e: Exception) {
            PaymentResult(
                success = false,
                error = "Ошибка инициализации платежа: ${e.message}"
            )
        }
    }

    actual suspend fun confirmPayment(confirmationUrl: String, paymentMethodType: String?): PaymentResult {
        val clientApplicationKey = BuildKonfig.YOOKASSA_CLIENT_APPLICATION_KEY
        val shopId = BuildKonfig.YOOKASSA_SHOP_ID

        // Проверяем, поддерживается ли тип платежного метода для SDK confirmation
        val supportedTypes = setOf("bank_card", "sbp", "sberbank")
        val isSupportedType = paymentMethodType?.lowercase() in supportedTypes

        return if (isSupportedType && paymentMethodType != null) {
            // Используем SDK confirmation для банковских карт, СБП и Сбербанка
            try {
                YooKassaActivityHelper.confirmPayment(
                    confirmationUrl = confirmationUrl,
                    paymentMethodType = paymentMethodType,
                    clientApplicationKey = clientApplicationKey,
                    shopId = shopId
                )
            } catch (e: Exception) {
                PaymentResult(
                    success = false,
                    error = "Ошибка подтверждения платежа через SDK: ${e.message}"
                )
            }
        } else {
            // Для других типов или если paymentMethodType = null используем простое открытие URL
            try {
                val activity = YooKassaActivityHelper.currentActivity
                if (activity != null) {
                    val intent = Intent(Intent.ACTION_VIEW, confirmationUrl.toUri())
                    activity.startActivity(intent)
                    PaymentResult(success = true)
                } else {
                    PaymentResult(
                        success = false,
                        error = "Activity не доступна для открытия URL"
                    )
                }
            } catch (e: Exception) {
                PaymentResult(
                    success = false,
                    error = "Ошибка открытия URL: ${e.message}"
                )
            }
        }
    }
}

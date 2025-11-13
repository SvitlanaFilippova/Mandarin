package com.mandarinkafe.mandarin.features.payment

import android.content.Intent
import androidx.core.net.toUri
import io.github.aakira.napier.Napier
import com.mandarinkafe.mandarin.shared.BuildKonfig

actual class YooKassaPaymentService {
    
    actual suspend fun initializePayment(
        amount: Double,
        orderId: String,
        userPhone: String
    ): PaymentResult {
        val clientApplicationKey = BuildKonfig.YOOKASSA_CLIENT_APPLICATION_KEY
        val shopId = BuildKonfig.YOOKASSA_SHOP_ID

        if (clientApplicationKey.isBlank() || shopId.isBlank()) {
            return PaymentResult(
                success = false,
                error = "Не настроены ключи ЮKassa (clientApplicationKey, shopId). Добавьте YOOKASSA_CLIENT_APPLICATION_KEY и YOOKASSA_SHOP_ID в apikeys.properties"
            )
        }

        return try {
            YooKassaActivityHelper.initializePayment(
                amount = amount,
                orderId = orderId,
                clientApplicationKey = clientApplicationKey,
                shopId = shopId,
                userPhone = userPhone
            )
        } catch (e: Exception) {
            Napier.e("PaymentFlow: [AndroidSDK] initializePayment - Exception", e)
            PaymentResult(
                success = false,
                error = "Ошибка инициализации платежа: ${e.message}"
            )
        }
    }

    actual suspend fun openPaymentUrl(confirmationUrl: String): PaymentResult {
        // Для Android SDK ЮKassa открытие confirmation_url происходит автоматически
        // после создания платежа на сервере через PaymentActivity.
        // Если нужно открыть URL вручную (например, для 3DS), можно использовать Intent
        
        return try {
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
            Napier.e("PaymentFlow: [AndroidSDK] openPaymentUrl - Exception", e)
            PaymentResult(
                success = false,
                error = "Ошибка открытия URL: ${e.message}"
            )
        }
    }
}

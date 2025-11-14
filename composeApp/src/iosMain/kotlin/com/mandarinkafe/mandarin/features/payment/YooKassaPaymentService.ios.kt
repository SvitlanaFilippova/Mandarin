package com.mandarinkafe.mandarin.features.payment

import com.mandarinkafe.mandarin.shared.BuildKonfig
import kotlinx.coroutines.suspendCancellableCoroutine

actual class YooKassaPaymentService {

    actual suspend fun initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
    ): PaymentResult {

        BuildKonfig.YOOKASSA_CLIENT_APPLICATION_KEY
        BuildKonfig.YOOKASSA_SHOP_ID

        return suspendCancellableCoroutine { continuation ->

//            YooKassaWrapper.initializePaymentSync(
//                amount = amount,
//                subtitle = subtitle,
//                userPhone = userPhone,
//                clientApplicationKey = clientKey,
//                shopId = shopId
//            ) { success, token, error ->
//
//                continuation.resume(
//                    PaymentResult(
//                        success = success,
//                        paymentToken = token,
//                        error = error
//                    )
//                )
//            }
        }
    }

    actual suspend fun openPaymentUrl(confirmationUrl: String): PaymentResult =
        suspendCancellableCoroutine { continuation ->

//            YooKassaWrapper.openPaymentUrlSync(
//                confirmationUrl = confirmationUrl
//            ) { success, _, error ->
//
//                continuation.resume(
//                    PaymentResult(
//                        success = success,
//                        error = error
//                    )
//                )
//            }
        }
}

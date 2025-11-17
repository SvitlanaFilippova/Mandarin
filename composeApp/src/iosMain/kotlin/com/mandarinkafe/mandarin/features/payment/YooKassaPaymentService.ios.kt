package com.mandarinkafe.mandarin.features.payment

import com.mandarinkafe.mandarin.yookassa.YooKassaWrapper
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


@OptIn(ExperimentalForeignApi::class)
actual class YooKassaPaymentService {
    actual suspend fun initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String
    ): PaymentResult = suspendCancellableCoroutine { continuation ->
        YooKassaWrapper.shared().initializePaymentWithAmount(
            amount = amount,
            subtitle = subtitle,
            userPhone = userPhone,
            clientApplicationKey = "твой_clientApplicationKey",
            shopId = "твой_shopId"
        ) { success, paymentToken, error ->
            continuation.resume(
                PaymentResult(
                    success = success,
                    paymentToken = paymentToken,
                    error = error
                )
            )
        }
    }


    actual suspend fun openPaymentUrl(confirmationUrl: String): PaymentResult =
        suspendCancellableCoroutine { continuation ->
            YooKassaWrapper.shared().openPaymentUrlWithConfirmationUrl(
                confirmationUrl = confirmationUrl
            ) { success, _, error ->
                continuation.resume(
                    PaymentResult(
                        success = success,
                        paymentToken = null,
                        error = error
                    )
                )
            }
        }
}
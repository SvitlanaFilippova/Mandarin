package com.mandarinkafe.mandarin.features.payment

actual class YooKassaPaymentService {
    actual suspend fun initializePayment(
        amount: Float,
        orderId: String
    ): PaymentResult {
        TODO("Not yet implemented - нужно реализовать инициализацию через YooKassa SDK для Android")
    }

    actual suspend fun openPaymentUrl(confirmationUrl: String): PaymentResult {
        TODO("Not yet implemented - нужно реализовать открытие confirmation_url через YooKassa SDK для Android")
    }
}
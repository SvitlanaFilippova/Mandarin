package com.mandarinkafe.mandarin.features.payment

/**
 * Expect интерфейс для работы с YooKassaPayments SDK
 * Actual реализация будет в iosMain
 */
expect class YooKassaPaymentService {
    /**
     * Инициализация платежа
     */
    suspend fun initializePayment(
        amount: Double,
        currency: String,
        description: String
    ): PaymentResult
    
    /**
     * Подтверждение платежа
     */
    suspend fun confirmPayment(paymentId: String): PaymentResult
}

data class PaymentResult(
    val success: Boolean,
    val paymentId: String? = null,
    val error: String? = null
)


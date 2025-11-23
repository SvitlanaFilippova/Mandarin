package com.mandarinkafe.mandarin.features.payment

/**
 * Expect интерфейс для работы с YooKassaPayments SDK
 * Actual реализация будет в platform-specific модулях
 */
expect class YooKassaPaymentService {
    /**
     * Инициализация платежа и получение payment_token
     */
    suspend fun initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
        orderId: String,
    ): PaymentResult

    /**
     * Открытие confirmation_url для оплаты через SDK
     * @param confirmationUrl URL для подтверждения платежа
     * @return PaymentResult с результатом оплаты
     */
    suspend fun openPaymentUrl(confirmationUrl: String): PaymentResult
}

data class PaymentResult(
    val success: Boolean,
    val paymentToken: String? = null, // Одноразовый токен от SDK (действителен 1 час)
    val paymentId: String? = null,
    val error: String? = null,
)


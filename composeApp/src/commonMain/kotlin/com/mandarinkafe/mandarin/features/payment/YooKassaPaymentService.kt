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
     * Подтверждение платежа через SDK (3DS, СБП, Сбербанк) или открытие confirmation_url
     * @param confirmationUrl URL для подтверждения платежа
     * @param paymentMethodType Тип платежного метода от сервера (bank_card, sbp, sberbank и т.д.)
     * @return PaymentResult с результатом подтверждения
     */
    suspend fun confirmPayment(
        confirmationUrl: String,
        paymentMethodType: String? = null,
    ): PaymentResult
}

data class PaymentResult(
    val success: Boolean,
    val paymentToken: String? = null, // Одноразовый токен от SDK (действителен 1 час)
    val paymentMethodType: String? = null, // Тип платежного метода (BANK_CARD, SBERBANK, SBP и т.д.)
    val paymentId: String? = null,
    val error: String? = null,
)


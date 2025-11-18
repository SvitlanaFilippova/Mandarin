package com.mandarinkafe.mandarin.features.payment.domain.models

data class PaymentInfo(
    val paymentId: String?,
    val orderId: String,
    val status: PaymentStatus,
    val paid: Boolean,
    val amountValue: String?,
    val amountCurrency: String?,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val confirmationUrl: String? = null, // URL для подтверждения платежа
)


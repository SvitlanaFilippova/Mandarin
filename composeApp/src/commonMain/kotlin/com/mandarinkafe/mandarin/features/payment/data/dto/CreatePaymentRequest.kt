package com.mandarinkafe.mandarin.features.payment.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentRequest(
    @SerialName("payment_token")
    val paymentToken: String,
    @SerialName("order_id")
    val orderId: String,
    val amount: Double,
    val currency: String = "RUB",
    val description: String,
    @SerialName("return_url")
    val returnUrl: String? = null, // URL для возврата после оплаты (для iOS "умного платежа")
)


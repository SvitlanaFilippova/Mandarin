package com.mandarinkafe.mandarin.features.payment.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentRequest(
    val payment_token: String,
    val order_id: String,
    val amount: Double,
    val currency: String = "RUB",
    val description: String,
)


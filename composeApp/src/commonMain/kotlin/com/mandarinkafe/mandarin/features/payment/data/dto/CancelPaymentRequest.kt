package com.mandarinkafe.mandarin.features.payment.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CancelPaymentRequest(
    @SerialName("order_id")
    val orderId: String,
    val reason: String = "canceled_by_client",
)

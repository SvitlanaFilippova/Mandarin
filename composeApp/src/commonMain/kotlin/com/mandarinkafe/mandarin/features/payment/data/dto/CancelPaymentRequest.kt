package com.mandarinkafe.mandarin.features.payment.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CancelPaymentRequest(
    val order_id: String,
)


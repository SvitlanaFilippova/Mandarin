package com.mandarinkafe.mandarin.features.payment.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaymentAmountDto(
    val value: String,
    val currency: String,
)
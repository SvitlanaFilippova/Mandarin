package com.mandarinkafe.mandarin.features.payment.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentConfirmationDto(
    val type: String? = null,
    @SerialName("confirmation_url")
    val confirmationUrl: String? = null,
)
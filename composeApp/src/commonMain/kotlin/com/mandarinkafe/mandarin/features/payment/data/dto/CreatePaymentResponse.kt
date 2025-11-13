package com.mandarinkafe.mandarin.features.payment.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentResponse(
    val id: String? = null,
    val status: String? = null,
    val paid: Boolean? = null,
    val amount: PaymentAmountDto? = null,
    val description: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val confirmation: PaymentConfirmationDto? = null,
) : Response()

@Serializable
data class PaymentAmountDto(
    val value: String,
    val currency: String,
)

@Serializable
data class PaymentConfirmationDto(
    val type: String? = null,
    @SerialName("confirmation_url")
    val confirmationUrl: String? = null,
)


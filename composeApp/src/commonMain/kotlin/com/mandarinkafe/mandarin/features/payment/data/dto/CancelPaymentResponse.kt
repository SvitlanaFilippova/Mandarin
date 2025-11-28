package com.mandarinkafe.mandarin.features.payment.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CancelPaymentResponse(
    val success: Boolean? = null,
    val message: String? = null,
    @SerialName("payment_id")
    val paymentId: String? = null,
    val status: String? = null,
) : Response()


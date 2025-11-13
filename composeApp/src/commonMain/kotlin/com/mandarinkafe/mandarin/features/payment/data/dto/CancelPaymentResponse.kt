package com.mandarinkafe.mandarin.features.payment.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class CancelPaymentResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val payment_id: String? = null,
    val status: String? = null,
) : Response()


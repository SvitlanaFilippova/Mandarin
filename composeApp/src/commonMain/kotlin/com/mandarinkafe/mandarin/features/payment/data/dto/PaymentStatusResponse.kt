package com.mandarinkafe.mandarin.features.payment.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusResponse(
    val payment_id: String? = null,
    val order_id: String? = null,
    val status: String? = null,
    val paid: Boolean? = null,
    val amount_value: String? = null,
    val amount_currency: String? = null,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
) : Response()


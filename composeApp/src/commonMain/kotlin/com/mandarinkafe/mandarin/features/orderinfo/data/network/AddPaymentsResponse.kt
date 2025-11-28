package com.mandarinkafe.mandarin.features.orderinfo.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class AddPaymentsResponse(
    val orderId: String? = null,
    val error: String? = null,
) : Response()


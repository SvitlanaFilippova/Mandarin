package com.mandarinkafe.mandarin.features.orderinfo.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class CancelOrderResponse(
    val correlationId: String
) : Response()






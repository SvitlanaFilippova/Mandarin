package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailsResponse(
    val id: String,
    val timestamp: Long,
    val creationStatus: String? = null,
    val errorInfo: com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto? = null,
    val order: com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.IncomingOrderDto? = null,
    val correlationId: String? = null,
) : Response()


package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.IncomingOrderDto
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailsResponse(
    val id: String,
    val timestamp: Long,
    val creationStatus: String? = null,
    val errorInfo: ErrorInfoDto? = null,
    val order: IncomingOrderDto? = null,
    val paymentDeadline: Long? = null,
) : Response()


package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.dto.order.OrderDto

data class OrderInfoResponse(
    val correlationId: String,
    val orders: List<OrderDto>
) : Response()

package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.dto.order.OrderInfoDto

data class OrderInfoResponse(
    val correlationId: String,
    val orders: List<OrderInfoDto>
) : Response()

package com.mandarinkafe.mandarin.features.order.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

data class CreateDeliveryResponse(
    val correlationId: String,
    val orderInfo: OrderInfo
) : Response()
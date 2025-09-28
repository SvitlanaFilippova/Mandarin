package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto

data class OrderInfoResponseDto(
    val id: String,
    val timestamp: Long,
    val creationStatus: String?,
    val errorInfo: ErrorInfoDto?,
    val order: IncomingOrderDto?,
)

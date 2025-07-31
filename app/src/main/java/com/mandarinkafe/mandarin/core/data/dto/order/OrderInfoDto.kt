package com.mandarinkafe.mandarin.core.data.dto.order

import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto

data class OrderInfoDto(
    val id: String,
    val timestamp: Long,
    val creationStatus: String, // Enum: "Success" "InProgress" "Error"
    val errorInfo: ErrorInfoDto?, // Required only if "creationStatus"="Error".
    val order: OrderDto?, // Field is filled up if "creationStatus"="Success".
)
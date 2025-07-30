package com.mandarinkafe.mandarin.features.order.data.network.dto

data class OrderInfoDto(
    val id: String,
    val timestamp: Long,
    val creationStatus: String,
    val errorInfo: ErrorInfoDto?
)

package com.mandarinkafe.mandarin.features.order.domain.models

data class OrderInfo(
    val id: String,
    val timestamp: Long,
    val creationStatus: String,
    val errorInfo: ErrorInfo?
)

package com.mandarinkafe.mandarin.features.order.data.network.dto

data class OrderInfo(
    val id: String,
    val timestamp: Int,
    val creationStatus: String,
    val errorInfo: ErrorInfo
)

data class ErrorInfo(
    val code: String,
    val message: String,
    val errorReason: String,
    val additionalData: Any
)
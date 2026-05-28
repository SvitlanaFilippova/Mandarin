package com.mandarinkafe.mandarin.features.order.domain.models

data class ErrorInfo(
    val code: String,
    val message: String?,
    val userMessage: String?,
    val errorReason: String?,
)

package com.mandarinkafe.mandarin.features.order.domain.models

data class ErrorInfo(
    val code: String,
    val message: String?,
    val errorReason: String?,
    val additionalData: Any?
)
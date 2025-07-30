package com.mandarinkafe.mandarin.features.order.data.network.dto

data class ErrorInfoDto(
    val code: String,
    val message: String,
    val errorReason: String,
    val additionalData: Any
)
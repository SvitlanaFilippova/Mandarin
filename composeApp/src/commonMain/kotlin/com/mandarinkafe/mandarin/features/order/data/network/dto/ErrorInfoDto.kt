package com.mandarinkafe.mandarin.features.order.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorInfoDto(
    val code: String,
    val message: String?,
    val errorReason: String?,
)

package com.mandarinkafe.mandarin.core.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val name: String,
    val type: String // regular or one-time
)
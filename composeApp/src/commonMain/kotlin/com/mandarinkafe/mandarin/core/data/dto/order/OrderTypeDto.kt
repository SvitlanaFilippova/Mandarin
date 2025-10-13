package com.mandarinkafe.mandarin.core.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderTypeDto(
    val id: String,
    val name: String,
    val orderServiceType: String? = null,
)

package com.mandarinkafe.mandarin.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderType(
    val id: String,
    val name: String,
    val orderServiceType: String? = null,
)

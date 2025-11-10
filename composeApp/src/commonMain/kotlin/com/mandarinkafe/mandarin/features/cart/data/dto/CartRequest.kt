package com.mandarinkafe.mandarin.features.cart.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    val items: List<CartItemDto>,
    @SerialName("last_updated")
    val lastUpdated: Long = 0L
)


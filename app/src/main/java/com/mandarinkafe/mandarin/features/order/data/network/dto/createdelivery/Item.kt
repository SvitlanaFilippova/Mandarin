package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class Item(
    val productId: String,
    val modifiers: List<OrderModifier>,
    val price: Double,
    val amount: Double,
    val type: String, // product or compound
)
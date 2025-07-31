package com.mandarinkafe.mandarin.core.data.dto.order

data class ItemDto(
    val productId: String,
    val modifiers: List<OrderModifierDto>? = null,
    val price: Double,
    val amount: Double,
    val type: String,
    val comment: String = ""
)
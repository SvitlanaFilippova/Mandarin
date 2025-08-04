package com.mandarinkafe.mandarin.core.data.dto.order

data class OrderModifierDto(
    val productId: String,
    val amount: Double,
    val price: Double,
    val productGroupId: String,
)

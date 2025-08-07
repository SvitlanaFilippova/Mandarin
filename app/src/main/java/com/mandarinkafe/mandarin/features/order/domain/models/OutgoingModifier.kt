package com.mandarinkafe.mandarin.features.order.domain.models

data class OutgoingModifier(
    val productId: String,
    val amount: Double,
    val price: Double,
    val productGroupId: String,
)
package com.mandarinkafe.mandarin.features.order.domain.models

data class OutgoingOrderItem(
    val productId: String,
    val modifiers: List<OutgoingModifier>? = null,
    val price: Double,
    val positionId: String,
    val amount: Double,
    val type: String,
    val comment: String = "",
    @Transient
    val discountable: Boolean = true
)
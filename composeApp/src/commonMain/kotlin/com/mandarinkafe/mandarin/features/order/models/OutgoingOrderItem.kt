package com.mandarinkafe.mandarin.features.order.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingOrderItem(
    val productId: String,
    val modifiers: List<OutgoingModifier>? = null,
    val price: Double,
    val positionId: String,
    val amount: Double,
    val type: String,
    val comment: String = "",
    @kotlinx.serialization.Transient
    val discountable: Boolean = true
)
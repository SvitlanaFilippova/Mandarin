package com.mandarinkafe.mandarin.features.order.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingModifier(
    val productId: String,
    val productGroupId: String,
    val amount: Double,
    val price: Double,
    val positionId: String,
    @kotlinx.serialization.Transient
    val discountable: Boolean = true
)
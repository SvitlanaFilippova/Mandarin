package com.mandarinkafe.mandarin.features.orderinfo.domain.models

data class IncomingModifier(
    val id: String,
    val name: String,
    val amount: Double,
    val price: Double,
    val discountedPrice: Double?,
    val modifierGroupId: String,
    val modifierGroupName: String
)

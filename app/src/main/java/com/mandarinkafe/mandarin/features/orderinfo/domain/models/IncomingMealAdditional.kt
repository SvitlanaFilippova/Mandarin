package com.mandarinkafe.mandarin.features.orderinfo.domain.models

data class IncomingMealAdditional(
    val id: String,
    val name: String,
    val amount: Double,
    val price: Double,
    val discountedPrice: Double?,
)
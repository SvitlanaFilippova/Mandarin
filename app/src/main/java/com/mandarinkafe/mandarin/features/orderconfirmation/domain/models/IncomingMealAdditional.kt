package com.mandarinkafe.mandarin.features.orderconfirmation.domain.models

data class IncomingMealAdditional(
    val id: String,
    val name: String,
    val amount: Double,
    val price: Double
)
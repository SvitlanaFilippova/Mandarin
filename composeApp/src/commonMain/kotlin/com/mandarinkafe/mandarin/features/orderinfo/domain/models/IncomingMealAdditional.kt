package com.mandarinkafe.mandarin.features.orderinfo.domain.models

import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType

data class IncomingMealAdditional(
    val id: String,
    val name: String,
    val amount: Double,
    val price: Double,
    val discountedPrice: Double?,
    val weight: Float,
    val measureUnitType: MeasureUnitType,
    val isDeleted: Boolean = false,
    val isValidated: Boolean = false,
)
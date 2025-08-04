package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal

interface CalculateCartTotalWithDiscountUseCase {
    operator fun invoke(
        items: Map<CustomizedMeal, Int>,
        discountAmount: Int
    ): Double
}
package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint

interface ResolvePickupPointUseCase {
    operator fun invoke(items: Set<CustomizedMeal>): OrderPickupPoint
}
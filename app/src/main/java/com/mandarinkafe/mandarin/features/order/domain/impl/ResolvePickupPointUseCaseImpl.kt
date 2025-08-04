package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.menu.domain.models.MealPickupPoint
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint

class ResolvePickupPointUseCaseImpl : ResolvePickupPointUseCase {
    override fun invoke(items: Set<CustomizedMeal>): OrderPickupPoint {
        val points = items.map { it.meal.pickupPoint }.toSet()
        return when {
            points.containsAll(
                setOf(
                    MealPickupPoint.PIZZERIA,
                    MealPickupPoint.CAFE
                )
            ) -> OrderPickupPoint.BOTH

            points.contains(MealPickupPoint.PIZZERIA) -> OrderPickupPoint.PIZZERIA
            points.contains(MealPickupPoint.CAFE) -> OrderPickupPoint.CAFE
            else -> OrderPickupPoint.CAFE
        }
    }
}
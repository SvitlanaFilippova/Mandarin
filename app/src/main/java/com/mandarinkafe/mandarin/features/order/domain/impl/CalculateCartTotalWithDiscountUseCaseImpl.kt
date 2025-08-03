package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.order.domain.api.CalculateCartTotalWithDiscountUseCase

class CalculateCartTotalWithDiscountUseCaseImpl() : CalculateCartTotalWithDiscountUseCase {
    override fun invoke(
        items: Map<CustomizedMeal, Int>,
        discountAmount: Int
    ): Double {
        return items.entries.sumOf { (customizedMeal, quantity) ->
            val mealPrice = customizedMeal.meal.price.toDouble()
            val addsPrice = customizedMeal.adds.sumOf { it.price.toDouble() }
            val modifiersPrice = customizedMeal.modifiers.sumOf { group ->
                group.items.sumOf { it.price.toDouble() }
            }
            val fullPricePerItem = mealPrice + addsPrice + modifiersPrice
            val discountModifier = 1 - discountAmount / PERCENT_DIVISOR
            val discountedPricePerItem = if (customizedMeal.meal.discountable) {
                // если блюдо discountable, то скидка работает на всё
                fullPricePerItem * discountModifier
            } else {
                // иначе - только на добавки и модификаторы, но не на само блюдо
                mealPrice + (addsPrice + modifiersPrice) * discountModifier
            }
            val total = discountedPricePerItem * quantity
            total
        }

    }

    private companion object {
        const val PERCENT_DIVISOR = 100.0
    }
}
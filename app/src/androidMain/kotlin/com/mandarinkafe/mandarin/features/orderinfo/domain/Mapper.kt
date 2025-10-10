package com.mandarinkafe.mandarin.features.orderinfo.domain

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem

object Mapper {
    fun IncomingOrderItem.toCartItem(
        baseMeal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>
    ): CartItem {
        return CartItem(
            customizedMeal = CustomizedMeal(
                meal = baseMeal,
                adds = adds,
                modifiers = modifiers
            ),
            quantity = amount.toInt(),
            comment = comment
        )
    }

}
package com.mandarinkafe.mandarin.cart.data.mapper

import com.mandarinkafe.mandarin.cart.data.models.CartMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal

object CartMapper {

    fun Meal.toCartMeal(quantity: Int) = CartMeal(
        id = id,
        adds = adds,
        quantity = quantity,
    )
}
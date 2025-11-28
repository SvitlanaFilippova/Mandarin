package com.mandarinkafe.mandarin.features.cart.domain

import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal

object Mapper {
    fun Meal.toCartItem() = CartItem(
        customizedMeal = this.toCustomizedMeal()
    )

    fun CustomizedMeal.toCartItem() = CartItem(
        customizedMeal = this,
    )
}
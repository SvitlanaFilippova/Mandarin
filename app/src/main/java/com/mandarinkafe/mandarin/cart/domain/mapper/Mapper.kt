package com.mandarinkafe.mandarin.cart.domain.mapper

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal

fun Meal.toCartItem() = CartItem(
    meal = this,
    quantity = 0
)
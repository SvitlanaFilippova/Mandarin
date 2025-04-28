package com.mandarinkafe.mandarin.cart.ui.view_model

import com.mandarinkafe.mandarin.cart.domain.model.CartItem

fun Map<CartItem, Int>.getTotalQuantityByMealId(mealId: String): Int {
    return this.filter { it.key.meal.id == mealId }
        .values
        .sum()
}

fun CartItem.totalPrice(): Int {
    return this.meal.price + this.adds.sumOf { it.price }
}
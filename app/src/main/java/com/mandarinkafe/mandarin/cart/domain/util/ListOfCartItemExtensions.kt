package com.mandarinkafe.mandarin.cart.domain.util

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal

fun List<CartItem>.isSameInCart(meal: Meal): Boolean {
    return any { it.meal == meal }
}

fun List<CartItem>.isInCarById(mealId: String): Boolean {
    return any { it.meal.id == mealId }
}

fun List<CartItem>.quantityOfSame(meal: Meal): Int {
    return firstOrNull { it.meal == meal }?.quantity ?: 0
}

fun List<CartItem>.quantityById(mealId: String): Int {
    return firstOrNull { it.meal.id == mealId }?.quantity ?: 0
}

fun List<CartItem>.indexOfMeal(meal: Meal): Int {
    return indexOfFirst { it.meal == meal }
}
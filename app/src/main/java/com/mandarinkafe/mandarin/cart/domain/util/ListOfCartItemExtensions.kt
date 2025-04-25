package com.mandarinkafe.mandarin.cart.domain.util

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.menu.domain.models.Meal

fun List<CartItem>.isInCart(meal: Meal): Boolean {
    return any { it.meal == meal }
}

fun List<CartItem>.quantityOf(meal: Meal): Int {
    return firstOrNull { it.meal == meal }?.quantity ?: 0
}

fun List<CartItem>.getCartItem(meal: Meal): CartItem? {
    return firstOrNull { it.meal == meal }
}
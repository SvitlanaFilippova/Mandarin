package com.mandarinkafe.mandarin.cart.domain.model

import com.mandarinkafe.mandarin.menu.domain.models.Meal

data class CartItem(
    val meal: Meal,
    val quantity: Int
)
package com.mandarinkafe.mandarin.cart.domain.model

import com.mandarinkafe.mandarin.core.domain.models.Meal

data class CartItem(
    val meal: Meal,
    val quantity: Int
)
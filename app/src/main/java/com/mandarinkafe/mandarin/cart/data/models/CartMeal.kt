package com.mandarinkafe.mandarin.cart.data.models

import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional

data class CartMeal(
    val id: String,
    val adds: List<MealAdditional>,
    val quantity: Int
)
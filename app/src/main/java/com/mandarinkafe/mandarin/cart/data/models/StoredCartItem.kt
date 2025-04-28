package com.mandarinkafe.mandarin.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.MealAdditional

data class StoredCartItem(
    val mealId: String,
    val adds: List<MealAdditional>,
    val quantity: Int
)
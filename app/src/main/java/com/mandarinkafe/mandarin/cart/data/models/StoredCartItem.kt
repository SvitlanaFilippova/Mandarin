package com.mandarinkafe.mandarin.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class StoredCartItem(
    val mealId: String,
    val adds: List<MealAdditional>?,
    val modifiers: List<ModifierGroup>?,
    val quantity: Int
)
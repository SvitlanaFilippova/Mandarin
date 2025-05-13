package com.mandarinkafe.mandarin.features.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class StoredCartItem(
    val mealId: String,
    val addsIds: List<String>?,
    val modifiers: List<ModifierGroup>?,
    val quantity: Int
)
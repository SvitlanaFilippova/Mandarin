package com.mandarinkafe.mandarin.features.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class StoredCartItem(
    val mealId: String,
    val addsIds: List<String>?,
    val modifiers: List<ModifierGroup>?,
    val quantity: Int,
    val comment: String
)

fun StoredCartItem.sameAs(other: StoredCartItem): Boolean {
    return mealId == other.mealId &&
            addsIds.orEmpty() == other.addsIds.orEmpty() &&
            modifiers.orEmpty() == other.modifiers.orEmpty() &&
            comment == other.comment
}
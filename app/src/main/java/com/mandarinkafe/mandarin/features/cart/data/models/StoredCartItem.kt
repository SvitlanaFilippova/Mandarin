package com.mandarinkafe.mandarin.features.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import java.util.UUID

data class StoredCartItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val mealId: String,
    val addsIds: List<String>,
    val modifiers: List<ModifierGroup>,
    val quantity: Int,
    val comment: String
)

fun StoredCartItem.sameAs(other: StoredCartItem): Boolean {
    return mealId == other.mealId &&
            addsIds == other.addsIds &&
            modifiers == other.modifiers &&
            comment == other.comment
}
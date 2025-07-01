package com.mandarinkafe.mandarin.features.cart

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

fun StoredCartItem.sameAs(other: StoredCartItem): Boolean {
    return mealId == other.mealId &&
            addsIds.orEmpty() == other.addsIds.orEmpty() &&
            modifiers.orEmpty() == other.modifiers.orEmpty()
}

fun List<ModifierGroup>.validateBy(mealModifiers: List<ModifierGroup>): List<ModifierGroup> {
    return this.mapNotNull { selectedGroup ->
        val referenceGroup = mealModifiers.find { it.id == selectedGroup.id }
        if (referenceGroup != null) {
            val updatedItems = selectedGroup.items.mapNotNull { item ->
                referenceGroup.items.find { it.id == item.id }
            }
            if (updatedItems.isNotEmpty()) {
                selectedGroup.copy(items = updatedItems)
            } else {
                null
            }
        } else {
            null
        }
    }
}
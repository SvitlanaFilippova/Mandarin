package com.mandarinkafe.mandarin.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class StoredCartItem(
    val mealId: String,
    val addsIds: List<String>?,
    val modifiers: List<ModifierGroup>?,
    val quantity: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StoredCartItem) return false

        return mealId == other.mealId &&
                addsIds.orEmpty() == other.addsIds.orEmpty() &&
                modifiers.orEmpty() == other.modifiers.orEmpty()
    }

    override fun hashCode(): Int {
        var result = mealId.hashCode()
        result = 31 * result + addsIds.orEmpty().hashCode()
        result = 31 * result + modifiers.orEmpty().hashCode()
        return result
    }
}

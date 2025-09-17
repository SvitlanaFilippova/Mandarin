package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class MealAdditional(
    val id: String,
    val name: String,
    val price: Int,
    val discountable: Boolean,
    val orderItemType: String,
) {
    override fun equals(other: Any?): Boolean {
        return other is MealAdditional && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

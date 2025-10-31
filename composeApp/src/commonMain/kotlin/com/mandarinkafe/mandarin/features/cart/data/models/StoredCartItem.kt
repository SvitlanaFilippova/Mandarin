package com.mandarinkafe.mandarin.features.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import kotlin.random.Random

data class StoredCartItem(
    val id: String = generateId(),
    val name: String,
    val mealId: String,
    val addsIds: List<String>,
    val modifiers: List<ModifierGroup>,
    val quantity: Int,
    val comment: String,
) {
    companion object {
        private fun generateId(): String {
            return Random.nextLong().toString()
        }
    }
}






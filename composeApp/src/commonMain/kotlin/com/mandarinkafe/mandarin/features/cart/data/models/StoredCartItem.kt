package com.mandarinkafe.mandarin.features.cart.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import kotlin.random.Random

data class StoredCartItem(
    val id: String = generateId(),
    val mealId: String,
    val quantity: Int,
    val modifiers: List<ModifierGroup>,
    val addsIds: List<String>,
    val comment: String,
    val createdAt: Long, // время создания позиции
    val updatedAt: Long, // время последнего изменения позиции
) {
    companion object {
        private fun generateId(): String {
            return Random.nextLong().toString()
        }
    }
}






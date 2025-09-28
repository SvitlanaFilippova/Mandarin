package com.mandarinkafe.mandarin.features.cart.data

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.totalPrice

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

fun List<CartItem>.getTotalQuantityByMealId(mealId: String): Int =
    this.filter { it.customizedMeal.meal.id == mealId }
        .sumOf { it.quantity }

fun List<CartItem>.getTotalPriceByMealId(mealId: String): Int =
    this.filter { it.customizedMeal.meal.id == mealId }
        .sumOf { it.customizedMeal.totalPrice() * it.quantity }

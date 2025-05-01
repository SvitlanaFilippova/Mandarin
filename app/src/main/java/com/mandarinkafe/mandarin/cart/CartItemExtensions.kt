package com.mandarinkafe.mandarin.cart

import com.mandarinkafe.mandarin.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

fun Map<CartItem, Int>.getTotalQuantityByMealId(mealId: String): Int {
    return this.filter { it.key.meal.id == mealId }
        .values
        .sum()
}

fun Map<CartItem, Int>.getTotalPriceByMealId(mealId: String): Int {
    return this.filter { it.key.meal.id == mealId }.entries
        .sumOf { (item, quantity) ->
            item.totalPrice() * quantity
        }
}

fun CartItem.totalPrice(): Int {
    val addsTotal = adds.sumOf { it.price }
    val modifiersTotal = modifiers.sumOf { group -> group.items.sumOf { it.price } }
    return meal.price + addsTotal + modifiersTotal
}

fun CartItem.customizedText(): String {
    val addsText = if (adds.isNotEmpty()) {
        "+ " + adds.joinToString(", ") { it.name }
    } else {
        ""
    }
    val modifiersText = if (modifiers.size > 1) {
        modifiers.joinToString("; ") { group ->
            val itemsText = group.items.joinToString(", ") { it.name }
            "${group.name}: $itemsText"
        }
    } else {
        modifiers.joinToString("; ") { group ->
            val itemsText = group.items.joinToString(", ") { it.name }
            itemsText
        }
    }


    return listOfNotNull(
        addsText.takeIf { it.isNotBlank() },
        modifiersText.takeIf { it.isNotBlank() }
    ).joinToString(" • ")
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
            } else null
        } else null
    }
}

fun StoredCartItem.sameAs(other: StoredCartItem): Boolean {
    return mealId == other.mealId &&
            addsIds.orEmpty() == other.addsIds.orEmpty() &&
            modifiers.orEmpty() == other.modifiers.orEmpty()
}

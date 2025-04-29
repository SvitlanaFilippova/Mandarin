package com.mandarinkafe.mandarin.cart

import com.mandarinkafe.mandarin.cart.domain.model.CartItem

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

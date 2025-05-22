package com.mandarinkafe.mandarin.features.cart.domain.model.extensions

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem

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

fun CartItem.isCustomized(): Boolean {
    return modifiers.isNotEmpty() || adds.isNotEmpty()
}

fun CartItem.customizedText(): String {
//    return when (meal.editableType) {
//        EditableType.ADDABLE -> {
//            val allItems = buildList {
//                addAll(modifiers.flatMap { group ->
//                    group.items.map { "+ ${it.name}" }
//                })
//                addAll(adds.map { "+ ${it.name}" })
//            }
//            allItems.joinToString(", ")
//        }

    return if (meal.requireSelection) {
        modifiers.joinToString("; ") { group ->
            val itemsText = group.items.joinToString(", ") { it.name }
            "${group.name}: $itemsText"
        }
    } else if (meal.isAddable || meal.isModifiable) {

        val allItems = buildList {
            addAll(modifiers.flatMap { group ->
                group.items.map { "+ ${it.name}" }
            })
            addAll(adds.map { "+ ${it.name}" })
        }
        allItems.joinToString(", ")
    } else {
        ""
    }

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

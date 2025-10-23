package com.mandarinkafe.mandarin.features.cart.data

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.data.local.CartItemInsertParams
import com.mandarinkafe.mandarin.features.cart.data.local.JsonAdapters
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.models.RecommendsSchemaRule
import com.mandarinkafe.mandarin.shared.database.Cart_items

object Mapper {

    fun StoredCartItem.toParams() = CartItemInsertParams(
        id = id,
        name = name,
        mealId = mealId,
        addsJson = JsonAdapters.listToJson(addsIds),
        modifiersJson = JsonAdapters.modsToJson(modifiers),
        quantity = quantity.toLong(),
        comment = comment,
    )

    fun Cart_items.toStoredCartItem(): StoredCartItem {
        val adds: List<String> = JsonAdapters.jsonToList(addsIds)
        val modifierGroups: List<ModifierGroup> = JsonAdapters.jsonToMods(modifiers)
        return StoredCartItem(
            id = id,
            mealId = mealId,
            name = name,
            addsIds = adds,
            modifiers = modifierGroups,
            quantity = quantity.toInt(),
            comment = comment.orEmpty()
        )
    }

    fun CartItem.toStoredCartItem() = StoredCartItem(
        id = id,
        name = name,
        mealId = customizedMeal.meal.id,
        addsIds = customizedMeal.adds.map { it.id },
        modifiers = customizedMeal.modifiers,
        quantity = quantity,
        comment = comment
    )

    fun RecommendsSchemaDto.toDomain() = RecommendsSchemaRule(
        sourceName = sourceName ?: "",
        excludeSku = excludeSku ?: emptyList<String>(),
        recommendedSku = recommendedSku ?: emptyList(),
        isSeparate = isSeparate
    )

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
}

package com.mandarinkafe.mandarin.features.cart.data

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.models.RecommendsSchemaRule

object Mapper {

    fun CartItem.toStoredCartItem() = StoredCartItem(
        id = id,
        name = name,
        mealId = customizedMeal.meal.id,
        addsIds = customizedMeal.adds.map { it.id },
        modifiers = customizedMeal.modifiers,
        quantity = quantity,
        comment = comment
    )

    fun StoredCartItem.toCustomizedMeal(
        meal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>
    ) = CustomizedMeal(
        meal = meal,
        adds = adds,
        modifiers = modifiers,
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

package com.mandarinkafe.mandarin.features.cart.data

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.dto.CartItemDto
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.data.local.CartItemInsertParams
import com.mandarinkafe.mandarin.features.cart.data.local.JsonAdapters
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.models.RecommendsSchemaRule
import com.mandarinkafe.mandarin.features.menu.domain.toMealAdditional
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
        timestamp = timestamp,
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
            comment = comment.orEmpty(),
            timestamp = timestamp
        )
    }

    fun CartItem.toStoredCartItem(timestamp: Long = 0L) = StoredCartItem(
        id = id,
        name = name,
        mealId = customizedMeal.meal.id,
        addsIds = customizedMeal.adds.map { it.id },
        modifiers = customizedMeal.modifiers,
        quantity = quantity,
        comment = comment,
        timestamp = timestamp
    )

    fun StoredCartItem.toCustomizedMeal(
        meal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>,
    ) = CustomizedMeal(
        meal = meal,
        adds = adds,
        modifiers = modifiers,
    )

    fun RecommendsSchemaDto.toDomain(): RecommendsSchemaRule {
        return RecommendsSchemaRule(
            sourceName = sourceName.orEmpty(),
            excludeSku = excludeSku?.split(";")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),
            recommendedSku = recommendedSku?.split(";")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),
            isSeparate = isSeparate
        )
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
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    fun StoredCartItem.toDto(): CartItemDto {
        val modifierIds = modifiers.associate { group ->
            group.id to group.items.map { it.id }
        }
        return CartItemDto(
            id = id,
            mealId = mealId,
            addsIds = addsIds,
            modifierIds = modifierIds,
            quantity = quantity,
            comment = comment,
            timestamp = timestamp,
        )
    }

    fun CartItemDto.toStored(menuCache: MenuCache): StoredCartItem? {
        val baseMeal = menuCache.getMealById(mealId) ?: return null

        // Получаем добавки
        val adds = addsIds.mapNotNull { addId ->
            menuCache.getMealById(addId)?.toMealAdditional()
        }

        // Получаем модификаторы из базового блюда
        val resolvedModifiers = modifierIds.mapNotNull { (groupId, itemIds) ->
            val referenceGroup = baseMeal.modifiers.find { it.id == groupId }
            referenceGroup?.copy(
                items = itemIds.mapNotNull { itemId ->
                    referenceGroup.items.find { it.id == itemId }
                }
            )?.takeIf { it.items.isNotEmpty() }
        }

        return StoredCartItem(
            id = id,
            name = baseMeal.name,
            mealId = mealId,
            addsIds = addsIds,
            modifiers = resolvedModifiers,
            quantity = quantity,
            comment = comment,
            timestamp = timestamp,
        )
    }
}

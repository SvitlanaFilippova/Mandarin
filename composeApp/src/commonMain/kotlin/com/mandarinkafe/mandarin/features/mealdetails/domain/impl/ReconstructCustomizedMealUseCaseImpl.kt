package com.mandarinkafe.mandarin.features.mealdetails.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.mealdetails.domain.api.ReconstructCustomizedMealUseCase
import com.mandarinkafe.mandarin.features.menu.domain.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.Success

class ReconstructCustomizedMealUseCaseImpl(
    private val menuCache: MenuCache,
) : ReconstructCustomizedMealUseCase {
    override suspend fun invoke(
        mealId: String,
        addsIds: List<String>,
        modifierIds: Map<String, List<String>>,
        comment: String,
        cartItemId: String?,
    ): Resource<CartItem> {
        // Получаем базовое блюдо
        val baseMeal = menuCache.getMealById(mealId)
        if (baseMeal == null) {
            return ErrorEmptyData()
        }

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

        // Создаем CustomizedMeal
        val customizedMeal = CustomizedMeal(
            meal = baseMeal,
            adds = adds,
            modifiers = resolvedModifiers
        )

        // Создаем CartItem
        val cartItem = if (cartItemId != null) {
            CartItem(
                id = cartItemId,
                customizedMeal = customizedMeal,
                comment = comment
            )
        } else {
            CartItem(
                customizedMeal = customizedMeal,
                comment = comment
            )
        }

        return Success(cartItem)
    }
}


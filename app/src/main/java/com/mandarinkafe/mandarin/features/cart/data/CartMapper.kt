package com.mandarinkafe.mandarin.features.cart.data

import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.db.Cart_items
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.data.local.CartItemInsertParams
import com.mandarinkafe.mandarin.features.cart.data.local.JsonAdapters
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule

object CartMapper {

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

    fun StoredCartItem.toCustomizedMeal(
        meal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>
    ) = CustomizedMeal(
        meal = meal,
        adds = adds,
        modifiers = modifiers,
    )

    fun Meal.toCartItem() = CartItem(
        customizedMeal = this.toCustomizedMeal(),
        name = name
    )

    fun CustomizedMeal.toCartItem() = CartItem(
        customizedMeal = this,
        name = meal.name
    )

    fun RecommendsSchemaDto.toDomain() = RecommendsSchemaRule(
        sourceName = sourceName ?: "",
        excludeSku = excludeSku ?: emptyList<String>(),
        recommendedSku = recommendedSku ?: emptyList()
    )
}
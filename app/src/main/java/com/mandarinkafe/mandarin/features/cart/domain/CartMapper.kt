package com.mandarinkafe.mandarin.features.cart.domain

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract

object CartMapper {
    fun CartItem.toStoredCartItem(quantity: Int): StoredCartItem {
        val storedCartItem = StoredCartItem(
            mealId = customizedMeal.meal.id,
            quantity = quantity,
            addsIds = customizedMeal.adds.map { it.id },
            modifiers = customizedMeal.modifiers,
            comment = comment
        )
        return storedCartItem
    }

    fun StoredCartItem.toCartItem(
        meal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>
    ) = CartItem(
        customizedMeal = toCustomizedMeal(
            meal = meal,
            adds = adds,
            modifiers = modifiers
        ),
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

    fun Meal.toAddToCartEvent(): CartContract.CartEvent.AddToCart {
        return CartContract.CartEvent.AddToCart(CartItem(CustomizedMeal(meal = this)))
    }

    fun Meal.toCartItem() = CartItem(
        customizedMeal = CustomizedMeal(
            meal = this
        )
    )

    fun CustomizedMeal.toCartItem() = CartItem(customizedMeal = this)

    fun Meal.toRemoveFromCartNow(): CartContract.CartEvent.RemoveFromCartByMeal {
        return CartContract.CartEvent.RemoveFromCartByMeal(meal = this)
    }

    fun RecommendsSchemaDto.toDomain() = RecommendsSchemaRule(
        sourceName = sourceName ?: "",
        excludeSku = excludeSku ?: emptyList<String>(),
        recommendedSku = recommendedSku ?: emptyList()
    )
}

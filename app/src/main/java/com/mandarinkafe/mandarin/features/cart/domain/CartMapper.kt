package com.mandarinkafe.mandarin.features.cart.domain

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract

object CartMapper {

    fun CustomizedMeal.toStoredCartItem(quantity: Int) = StoredCartItem(
        mealId = meal.id,
        quantity = quantity,
        addsIds = adds.map { it.id },
        modifiers = modifiers
    )

    fun StoredCartItem.toCartItem(
        meal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>
    ) = CustomizedMeal(
        meal = meal,
        adds = adds,
        modifiers = modifiers
    )

    fun Meal.toAddToCartEvent(): CartContract.CartEvent.AddToCart {
        return CartContract.CartEvent.AddToCart(CustomizedMeal(meal = this))
    }

    fun Meal.toRemoveFromCartNow(): CartContract.CartEvent.RemoveFromCartByMeal {
        return CartContract.CartEvent.RemoveFromCartByMeal(meal = this)
    }

    fun Meal.toCartItem() = CustomizedMeal(
        meal = this,
        adds = emptyList()
    )
}
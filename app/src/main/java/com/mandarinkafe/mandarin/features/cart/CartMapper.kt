package com.mandarinkafe.mandarin.features.cart


import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent

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

    fun Meal.toAddToCartEvent(): CartEvent.AddToCart {
        return CartEvent.AddToCart(CustomizedMeal(meal = this))
    }

    fun Meal.toRemoveFromCartNow(): CartEvent.RemoveFromCartByMeal {
        return CartEvent.RemoveFromCartByMeal(meal = this)
    }

    fun Meal.toCartItem() = CustomizedMeal(
        meal = this,
        adds = emptyList()
    )
}

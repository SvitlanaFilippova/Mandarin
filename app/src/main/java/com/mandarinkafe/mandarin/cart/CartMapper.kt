package com.mandarinkafe.mandarin.cart

import com.mandarinkafe.mandarin.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

object CartMapper {

    fun CartItem.toStoredCartItem(quantity: Int) = StoredCartItem(
        mealId = meal.id,
        quantity = quantity,
        addsIds = adds.map { it.id },
        modifiers = modifiers
    )

    fun StoredCartItem.toCartItem(
        meal: Meal,
        adds: List<MealAdditional>,
        modifiers: List<ModifierGroup>
    ) = CartItem(
        meal = meal,
        adds = adds,
        modifiers = modifiers
    )

    fun Meal.toAddToCartEvent(): Event.AddToCart {
        return Event.AddToCart(CartItem(meal = this))
    }

    fun Meal.toRemoveFromCartNow(): Event.RemoveFromCartByMeal {
        return Event.RemoveFromCartByMeal(meal = this)
    }

    fun Meal.toCartItem() = CartItem(
        meal = this,
        adds = emptyList()
    )
}

package com.mandarinkafe.mandarin.cart

import com.mandarinkafe.mandarin.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract.Event
import com.mandarinkafe.mandarin.core.domain.models.Meal

object CartMapper {

    fun CartItem.toStoredCartItem(quantity: Int) = StoredCartItem(
        mealId = meal.id,
        adds = adds,
        quantity = quantity,
    )

    fun Meal.toAddToCartEvent(): Event.AddToCart {
        return Event.AddToCart(CartItem(meal = this))
    }

    // TODO проверить логику, неравильно сейчас!!!!
    fun Meal.toRemoveFromCartEvent(): Event.RemoveFromCart {
        return Event.RemoveFromCart(CartItem(meal = this))
    }
}

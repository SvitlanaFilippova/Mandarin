package com.mandarinkafe.mandarin.features.cart.domain.models

import com.mandarinkafe.mandarin.core.domain.models.CartItem

sealed class MealAddResult {
    data class AlreadyExistBaseMeal(val existing: CartItem) : MealAddResult()
    data object Added : MealAddResult()
}
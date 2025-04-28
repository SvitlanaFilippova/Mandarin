package com.mandarinkafe.mandarin.cart.domain.model

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional

data class CartItem(
    val meal: Meal,
    /**
    Выбранные добавки для пиццы
     */
    val adds: List<MealAdditional> = emptyList<MealAdditional>(),
)
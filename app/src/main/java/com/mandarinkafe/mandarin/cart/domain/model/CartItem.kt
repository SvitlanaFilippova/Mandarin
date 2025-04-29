package com.mandarinkafe.mandarin.cart.domain.model

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class CartItem(
    val meal: Meal,

    /**
    Выбранные добавки для пиццы
     */
    val adds: List<MealAdditional> = emptyList<MealAdditional>(),

    /**
    Выбранные модификаторы (по группаам)
     */
    val modifiers: List<ModifierGroup> = emptyList<ModifierGroup>()
)
package com.mandarinkafe.mandarin.menu.domain.mappers

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional

fun Meal.toMealAdditional() = MealAdditional(
    id = id,
    name = name,
    weight = weight,
    price = price,
    isHidden = isHidden
)
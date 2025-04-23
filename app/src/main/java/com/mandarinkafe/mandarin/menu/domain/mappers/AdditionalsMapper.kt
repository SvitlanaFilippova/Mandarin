package com.mandarinkafe.mandarin.menu.domain.mappers

import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory

fun Meal.toMealAdditional() = MealAdditional(
    id = id,
    name = name,
    weight = weight,
    price = price,
    isHidden = isHidden
)

fun MealCategory.toMealAdditionalCategory() = MealAdditionalCategory(
    id = id,
    name = name,
    mealAdditionals = meals?.map { it.toMealAdditional() } ?: emptyList(),
    isHidden = isHidden
)
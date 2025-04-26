package com.mandarinkafe.mandarin.menu.domain.mappers

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.applyTypography

fun Meal.toMealAdditional() = MealAdditional(
    id = id,
    name = name.applyTypography(),
    weight = weight,
    price = price,
    isHidden = isHidden
)

fun MealCategory.toMealAdditionalCategory() = MealAdditionalCategory(
    id = id,
    name = name.applyTypography(),
    mealAdditionals = meals?.map { it.toMealAdditional() } ?: emptyList(),
    isHidden = isHidden
)
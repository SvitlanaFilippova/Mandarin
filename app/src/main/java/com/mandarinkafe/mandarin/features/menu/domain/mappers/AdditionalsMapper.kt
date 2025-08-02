package com.mandarinkafe.mandarin.features.menu.domain.mappers

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.applyTypography

fun Meal.toMealAdditional() = MealAdditional(
    id = id,
    name = name.applyTypography(),
    price = price,
    orderItemType = orderItemType,
    discountable = discountable,
)

fun MealCategory.toMealAdditionalCategory() = MealAdditionalCategory(
    id = id,
    name = name.applyTypography(),
    items = meals?.map { it.toMealAdditional() } ?: emptyList(),
)
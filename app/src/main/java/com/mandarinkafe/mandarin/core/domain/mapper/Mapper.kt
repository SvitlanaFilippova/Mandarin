package com.mandarinkafe.mandarin.core.domain.mapper

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal

object Mapper {
    fun Meal.toCustomizedMeal() = CustomizedMeal(
        meal = this
    )
}
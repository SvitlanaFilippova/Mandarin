package com.mandarinkafe.mandarin.core.domain.models

object Mapper {

    fun Meal.toCustomizedMeal() = CustomizedMeal(
        meal = this
    )
}
package com.mandarinkafe.mandarin.menu.domain.models

import com.mandarinkafe.mandarin.core.domain.models.Meal

fun Meal.totalPrice(): Int {
    return price + adds.sumOf { it.price }
}
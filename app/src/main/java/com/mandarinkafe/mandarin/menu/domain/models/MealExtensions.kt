package com.mandarinkafe.mandarin.menu.domain.models

fun Meal.totalPrice(): Int {
    return price + adds.sumOf { it.price }
}
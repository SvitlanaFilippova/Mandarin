package com.mandarinkafe.mandarin.cart

import com.mandarinkafe.mandarin.menu.domain.models.Meal

object Cart {
    val meals: MutableList<Meal> = mutableListOf()

    fun addItem(meal: Meal) {
        meals.add(meal)
    }

    fun clear() {
        meals.clear()
    }
}
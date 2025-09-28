package com.mandarinkafe.mandarin.features.menu.presentation.models.extensions

import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem

// Функция для получения имени в зависимости от типа элемента
fun MenuItem.getName(): String? = when (this) {
    is MenuItem.HeaderItem -> categoryName
    is MenuItem.SubHeaderItem -> categoryName
    is MenuItem.MealItem.SingleMealItem -> meal.name
    is MenuItem.MealItem.MealRow -> "${left.name} / ${right.name}"
}


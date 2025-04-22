package com.mandarinkafe.mandarin.menu.domain.models

// Функция для получения имени в зависимости от типа элемента
fun MenuItem.getName(): String? = when (this) {
    is MenuItem.HeaderItem -> categoryName
    is MenuItem.SubHeaderItem -> categoryName
    is MenuItem.MealItem -> meal.name
}
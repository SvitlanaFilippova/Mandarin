package com.mandarinkafe.mandarin.menu.domain.models

// Функция для получения имени в зависимости от типа элемента
fun MenuRVItem.getName(): String? = when (this) {
    is MenuRVItem.HeaderItem -> categoryName
    is MenuRVItem.SubHeaderItem -> categoryName
    is MenuRVItem.MealItem -> meal.name
}
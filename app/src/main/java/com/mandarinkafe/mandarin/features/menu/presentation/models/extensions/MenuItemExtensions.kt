package com.mandarinkafe.mandarin.features.menu.presentation.models.extensions

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem

// Функция для получения имени в зависимости от типа элемента
fun MenuItem.getName(): String? = when (this) {
    is MenuItem.HeaderItem -> categoryName
    is MenuItem.SubHeaderItem -> categoryName
    is MenuItem.MealItem.SingleMealItem -> meal.name
    is MenuItem.MealItem.MealRow -> "${left.name} / ${right.name}"
}

fun List<MenuItem>.updateMeal(id: String, transform: (Meal) -> Meal): List<MenuItem> {
    return map { item ->
        when (item) {
            is MenuItem.MealItem.SingleMealItem ->
                if (item.meal.id == id) item.copy(meal = transform(item.meal)) else item

            is MenuItem.MealItem.MealRow -> {
                val newLeft = if (item.left.id == id) transform(item.left) else item.left
                val newRight = if (item.right.id == id) transform(item.right) else item.right
                if (newLeft != item.left || newRight != item.right)
                    item.copy(left = newLeft, right = newRight)
                else item
            }

            else -> item
        }
    }
}
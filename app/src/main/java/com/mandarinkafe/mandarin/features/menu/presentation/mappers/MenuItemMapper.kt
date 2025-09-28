package com.mandarinkafe.mandarin.features.menu.presentation.mappers

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import java.util.UUID

object MenuItemMapper {

    fun menuToMenuItems(menu: List<MealCategory>?): List<MenuItem> {
        return buildList {
            menu?.forEach { category ->
                val hasMeals = !category.meals.isNullOrEmpty()
                val visibleSubcategories =
                    category.subCategories.orEmpty().filter { !it.meals.isNullOrEmpty() }

                val hasVisibleSubcategories = visibleSubcategories.isNotEmpty()

                // Добавляем HeaderItem, если есть блюда или подкатегории с блюдами
                if (hasMeals || hasVisibleSubcategories) {
                    this += MenuItem.HeaderItem(
                        categoryName = category.name,
                        sku = category.id,
                        subCategoriesNames = visibleSubcategories.map { it.name }
                            .takeIf { it.isNotEmpty() },
                        tabIcon = category.tabIcon,
                        description = category.description,
                        id = "header_${UUID.randomUUID()}"
                    )
                }

                // Добавляем блюда самой категории
                if (hasMeals) {
                    addAll(groupMealsToItems(category.meals))
                }

                // Добавляем подкатегории (если есть)
                visibleSubcategories.forEach { subCategory ->
                    this += MenuItem.SubHeaderItem(
                        categoryName = subCategory.name,
                        sku = subCategory.id,
                        description = subCategory.description,
                        id = "sub_${UUID.randomUUID()}"
                    )
                    subCategory.meals?.let { meals ->
                        addAll(groupMealsToItems(meals))
                    }
                }
            }
        }
    }

    // 👇 Логика разбиения на SingleMealItem / MealRow
    private fun groupMealsToItems(source: List<Meal>): List<MenuItem> {
        val result = mutableListOf<MenuItem>()
        var i = 0
        while (i < source.size) {
            val current = source[i]
            val next = source.getOrNull(i + 1)
            val currentIsCompact = current.description.isBlank()
            val nextIsCompact = next?.description?.isBlank() == true

            if (currentIsCompact && nextIsCompact) {
                result += MenuItem.MealItem.MealRow(
                    left = current,
                    right = next,
                    id = "row_${UUID.randomUUID()}"
                )
                i += 2
            } else {
                result += MenuItem.MealItem.SingleMealItem(
                    meal = current,
                    id = "meal_${UUID.randomUUID()}"
                )
                i += 1
            }
        }
        return result
    }
}
package com.mandarinkafe.mandarin.features.menu.ui.mappers

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem

object MenuItemMapper {

    fun menuToMenuItems(menu: List<MealCategory>?): List<MenuItem> {
        val menuItems = buildList<MenuItem> {
            menu?.forEach { category ->
                if (!category.subCategories.isNullOrEmpty()) {
                    // Добавляем Header
                    this += MenuItem.HeaderItem(
                        categoryName = category.name,
                        subCategoriesNames = category.subCategories
                            .filter { !it.meals.isNullOrEmpty() }
                            .map { it.name },
                        tabIcon = category.tabIcon,
                        description = category.description,
                        sku = category.id
                    )

                    // Обработка подкатегорий
                    category.subCategories.forEach { subCategory ->
                        if (!subCategory.meals.isNullOrEmpty()) {
                            this += MenuItem.SubHeaderItem(
                                categoryName = subCategory.name,
                                description = subCategory.description,
                                sku = subCategory.id
                            )
                            this += groupMealsToItems(subCategory.meals)
                        }
                    }
                } else {
                    // Категория без подкатегорий
                    if (!category.meals.isNullOrEmpty()) {
                        this += MenuItem.HeaderItem(
                            categoryName = category.name,
                            subCategoriesNames = null,
                            tabIcon = category.tabIcon,
                            description = category.description,
                            sku = category.id
                        )
                        this += groupMealsToItems(category.meals)
                    }
                }
            }
        }
        return menuItems
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
                result += MenuItem.MealItem.MealRow(current, next)
                i += 2
            } else {
                result += MenuItem.MealItem.SingleMealItem(current)
                i += 1
            }
        }
        return result
    }
}
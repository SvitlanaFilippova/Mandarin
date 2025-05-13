package com.mandarinkafe.mandarin.features.menu.domain.mappers

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem

object MenuRVItemMapper {

    fun menuToMenuItems(menu: List<MealCategory>?): List<MenuItem> {
        val menuItems = buildList<MenuItem> {
            menu?.forEach { category ->
                if (!category.subCategories.isNullOrEmpty()) {
                    this += MenuItem.HeaderItem(
                        categoryName = category.name,
                        subCategoriesNames = buildList {
                            category.subCategories.filter { !it.meals.isNullOrEmpty() }
                                .forEach { this += it.name }
                        },
                        tabIcon = category.tabIcon,
                        description = category.description,
                        id = category.id
                    )

                    category.subCategories.forEach { subCategory ->
                        if (!subCategory.meals.isNullOrEmpty()) {
                            this += MenuItem.SubHeaderItem(
                                categoryName = subCategory.name,
                                description = subCategory.description,
                                id = subCategory.id
                            )
                            this += subCategory.meals.map {
                                MenuItem.MealItem(
                                    meal = it
                                )
                            }
                        }
                    }
                } else {
                    if (!category.meals.isNullOrEmpty()) {
                        this += MenuItem.HeaderItem(
                            categoryName = category.name,
                            subCategoriesNames = null,
                            tabIcon = category.tabIcon,
                            description = category.description,
                            id = category.id
                        )
                        this += category.meals.map {
                            MenuItem.MealItem(
                                it
                            )
                        }
                    }
                }
            }
        }
        return menuItems
    }
}
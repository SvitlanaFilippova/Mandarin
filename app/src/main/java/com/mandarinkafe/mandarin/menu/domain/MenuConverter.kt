package com.mandarinkafe.mandarin.menu.domain

import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.util.RVItem

object MenuConverter {

    fun menuToMenuItems(menu: List<MealCategory>?): List<RVItem> {
        val menuItems = buildList<RVItem> {
            menu?.forEach { category ->
                if (!category.subCategories.isNullOrEmpty()) {
                    this += MenuRVItem.HeaderItem(
                        categoryName = category.name,
                        subCategoriesNames = buildList {
                            category.subCategories.forEach { this += it.name }
                        },
                        tabIcon = category.tabIcon,
                        description = category.description,
                        id = category.id
                    )

                    category.subCategories.forEach { subCategory ->
                        if (!subCategory.meals.isNullOrEmpty()) {
                            this += MenuRVItem.SubHeaderItem(
                                categoryName = subCategory.name,
                                description = subCategory.description,
                                id = subCategory.id
                            )
                            this += subCategory.meals.map {
                                MenuRVItem.MealItem(
                                    meal = it
                                )
                            }
                        }
                    }
                } else {
                    if (!category.meals.isNullOrEmpty()) {
                        this += MenuRVItem.HeaderItem(
                            categoryName = category.name,
                            subCategoriesNames = null,
                            tabIcon = category.tabIcon,
                            description = category.description,
                            id = category.id
                        )
                        this += category.meals.map {
                            MenuRVItem.MealItem(
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
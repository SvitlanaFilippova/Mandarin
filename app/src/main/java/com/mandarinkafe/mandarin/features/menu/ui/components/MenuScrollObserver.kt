package com.mandarinkafe.mandarin.features.menu.ui.components

import androidx.compose.foundation.lazy.LazyListItemInfo
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem

fun getVisibleCategoryIndexes(
    visibleItems: List<LazyListItemInfo>,
    menuItems: List<MenuItem>,
    categoriesNames: List<String>
): Pair<Int?, Int?> {
    val firstVisibleIndex =
        visibleItems.firstOrNull { it.offset >= 0 }?.index ?: return null to null
    val item = menuItems.getOrNull(firstVisibleIndex) ?: return null to null

    val referenceMeal = when (item) {
        is MenuItem.MealItem.SingleMealItem -> item.meal
        is MenuItem.MealItem.MealRow -> item.left // или right, как решишь
        else -> return null to null
    }

    val indexInMenu = menuItems.indexOfFirst {
        it is MenuItem.MealItem.SingleMealItem && it.meal.id == referenceMeal.id
    }.takeIf { it >= 0 } ?: return null to null

    val parentCategory = menuItems
        .take(indexInMenu)
        .lastOrNull { it is MenuItem.HeaderItem } as? MenuItem.HeaderItem

    val newCategoryIndex = parentCategory?.let {
        categoriesNames.indexOf(it.categoryName).takeIf { i -> i >= 0 }
    }

    val newSubCategoryIndex = parentCategory?.let { category ->
        val parentSubCategory = menuItems
            .take(indexInMenu)
            .lastOrNull { it is MenuItem.SubHeaderItem } as? MenuItem.SubHeaderItem
        parentSubCategory?.let {
            category.subCategoriesNames?.indexOf(it.categoryName)
        }
    }

    return newCategoryIndex to newSubCategoryIndex
}
package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.lazy.LazyListItemInfo
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.util.Constants.SOUS_DLYA_KOROCHEK_SKU

fun getVisibleCategoryIndexes(
    visibleItems: List<LazyListItemInfo>,
    menuItems: List<MenuItem>,
    categoriesNames: List<String>
): Pair<Int?, Int?> {
    val firstVisibleIndex = visibleItems.firstOrNull { it.offset >= 0 }?.index
    val item = firstVisibleIndex?.let { menuItems.getOrNull(it) }
    val referenceMeal = when (item) {
        is MenuItem.MealItem.SingleMealItem -> item.meal
        is MenuItem.MealItem.MealRow -> item.left
        else -> null
    }

    if (firstVisibleIndex == null || item == null || referenceMeal == null) {
        return null to null
    }
    if (referenceMeal.sku == SOUS_DLYA_KOROCHEK_SKU) {
        return null to null
    }

    val indexInMenu = menuItems.indexOfFirst {
        it is MenuItem.MealItem.SingleMealItem && it.meal.id == referenceMeal.id
    }
    if (indexInMenu < 0) return null to null

    val parentCategory = menuItems
        .take(indexInMenu)
        .lastOrNull { it is MenuItem.HeaderItem } as? MenuItem.HeaderItem

    val newCategoryIndex = parentCategory?.categoryName?.let {
        categoriesNames.indexOf(it).takeIf { i -> i >= 0 }
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
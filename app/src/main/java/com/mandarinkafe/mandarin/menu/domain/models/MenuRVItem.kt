package com.mandarinkafe.mandarin.menu.domain.models

import com.mandarinkafe.mandarin.core.ui.RVItem


sealed interface MenuRVItem : RVItem {
    data class HeaderItem(
        val categoryName: String,
        var subCategoriesNames: List<String>?,
        val tabIcon: String?
    ) : MenuRVItem

    data class SubHeaderItem(val categoryName: String) : MenuRVItem
    data class MealItem(val meal: Meal) : MenuRVItem
}


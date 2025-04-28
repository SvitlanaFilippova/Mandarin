package com.mandarinkafe.mandarin.menu.domain.models

import com.mandarinkafe.mandarin.core.domain.models.Meal

sealed interface MenuItem {
    data class HeaderItem(
        val categoryName: String,
        var subCategoriesNames: List<String>?,
        val tabIcon: String?,
        val description: String,
        val id: String
    ) : MenuItem

    data class SubHeaderItem(val categoryName: String, val description: String, val id: String) :
        MenuItem

    data class MealItem(val meal: Meal) : MenuItem
}
package com.mandarinkafe.mandarin.menu.domain.models

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
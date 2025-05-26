package com.mandarinkafe.mandarin.features.menu.ui.models

import com.mandarinkafe.mandarin.core.domain.models.Meal

sealed interface MenuItem {
    data class HeaderItem(
        val categoryName: String,
        val sku: String,
        var subCategoriesNames: List<String>?,
        val tabIcon: String?,
        val description: String,
    ) : MenuItem

    data class SubHeaderItem(
        val categoryName: String,
        val sku: String,
        val description: String
    ) : MenuItem

    sealed interface MealItem : MenuItem {
        data class SingleMealItem(val meal: Meal) : MealItem
        data class MealRow(val left: Meal, val right: Meal) : MealItem
    }
}
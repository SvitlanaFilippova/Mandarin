package com.mandarinkafe.mandarin.features.menu.presentation.models

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.core.domain.models.Meal

sealed interface MenuItem {

    @Stable
    data class HeaderItem(
        val categoryName: String,
        val sku: String,
        val subCategoriesNames: List<String>?,
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
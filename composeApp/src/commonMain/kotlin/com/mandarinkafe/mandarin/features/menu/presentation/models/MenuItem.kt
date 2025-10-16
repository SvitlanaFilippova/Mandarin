package com.mandarinkafe.mandarin.features.menu.presentation.models

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.core.domain.models.Meal

sealed interface MenuItem {
    val id: String

    @Stable
    data class HeaderItem(
        override val id: String,
        val categoryName: String,
        val sku: String,
        val subCategoriesNames: List<String>?,
        val tabIcon: String?,
        val description: String,
    ) : MenuItem

    data class SubHeaderItem(
        override val id: String,
        val categoryName: String,
        val sku: String,
        val description: String
    ) : MenuItem

    sealed interface MealItem : MenuItem {
        data class SingleMealItem(override val id: String, val meal: Meal) : MealItem
        data class MealRow(override val id: String, val left: Meal, val right: Meal) : MealItem
    }
}






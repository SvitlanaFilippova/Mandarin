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

    @Stable
    data class SubHeaderItem(
        override val id: String,
        val categoryName: String,
        val sku: String,
        val description: String,
    ) : MenuItem

    sealed interface MealItem : MenuItem {
        @Stable
        data class SingleMealItem(override val id: String, val meal: Meal) : MealItem

        @Stable
        data class MealRow(override val id: String, val left: Meal, val right: Meal) : MealItem
    }
}
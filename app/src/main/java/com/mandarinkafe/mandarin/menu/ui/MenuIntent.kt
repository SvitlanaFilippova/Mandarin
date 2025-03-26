package com.mandarinkafe.mandarin.menu.ui

import com.mandarinkafe.mandarin.menu.domain.models.Meal

sealed interface MenuIntent {
    data object LoadMenu : MenuIntent
    data class SelectCategory(val index: Int) : MenuIntent
    data class SelectSubCategory(val index: Int) : MenuIntent
    data class ToggleFavorite(val meal: Meal) : MenuIntent
    data class AddToCart(val meal: Meal) : MenuIntent
    data class RemoveFromCart(val meal: Meal) : MenuIntent
}
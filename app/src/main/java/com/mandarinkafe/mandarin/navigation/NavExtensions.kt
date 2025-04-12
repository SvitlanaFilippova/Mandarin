package com.mandarinkafe.mandarin.navigation

import androidx.navigation.NavController
import com.mandarinkafe.mandarin.navigation.NavRoutes.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ROUTE

fun NavController.navigateToSearchScreen(focusInput: Boolean) {
    this.navigate("$SEARCH_SCREEN_ROUTE/$focusInput")
}

fun NavController.navigateToMenuScreen() {
    this.navigate(MENU_SCREEN_ROUTE)
}

fun NavController.navigateToFavoritesScreen() {
    this.navigate(FAVORITES_SCREEN_ROUTE)
}

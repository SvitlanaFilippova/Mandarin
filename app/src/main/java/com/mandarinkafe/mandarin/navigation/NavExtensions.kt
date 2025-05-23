package com.mandarinkafe.mandarin.navigation

import androidx.navigation.NavController
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ROUTE

fun NavController.navigateToSearchScreen(focusInput: Boolean) {
    this.navigate("$SEARCH_SCREEN_ROUTE?focusInput=$focusInput") {
        launchSingleTop = true
        restoreState = true
        popUpTo(MENU_SCREEN_ROUTE) {
            saveState = true
        }
    }
}

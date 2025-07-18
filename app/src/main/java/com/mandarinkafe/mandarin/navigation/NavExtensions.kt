package com.mandarinkafe.mandarin.navigation

import androidx.navigation.NavController
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE

fun NavController.navigateToSearchScreen(focusInput: Boolean) {
    this.navigate("$SEARCH_SCREEN_ROUTE?focusInput=$focusInput") {
        launchSingleTop = true
        restoreState = true
        popUpTo(MENU_SCREEN_ROUTE) {
            saveState = true
        }
    }
}

fun NavController.navigateToOrder() {
    this.navigate(ORDER_SCREEN_ROUTE)
}

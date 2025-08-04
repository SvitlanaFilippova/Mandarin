package com.mandarinkafe.mandarin.navigation.extensions

import androidx.navigation.NavController
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MAIN_GRAPH
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_CONFIRMATION_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun NavController.navigateToSearchScreen(focusInput: Boolean) {
    this.navigate("$SEARCH_SCREEN_ROUTE?focusInput=$focusInput") {
        launchSingleTop = true
        restoreState = true
        popUpTo(MENU_SCREEN_ROUTE) {
            saveState = true
        }
    }
}

fun NavController.navigateToMenu() {
    this.navigate(MENU_SCREEN_ROUTE) {
        restoreState = true
    }
}

fun NavController.navigateToOrder() {
    this.navigate(ORDER_SCREEN_ROUTE)
}

fun NavController.navigateToAddress(address: Address? = null) {
    if (address == null) {
        this.navigate(ADDRESS_SCREEN_ROUTE)
    } else {
        val gson = Gson()
        val json = URLEncoder.encode(gson.toJson(address), StandardCharsets.UTF_8.toString())
        val route = "$ADDRESS_SCREEN_ROUTE/$json"
        this.navigate(route)
    }
}

fun NavController.navigateToMealDetails(meal: CustomizedMeal, isEditMode: Boolean) {
    val gson = Gson()
    val json =
        URLEncoder.encode(gson.toJson(meal), StandardCharsets.UTF_8.toString())
    val route = "$MEAL_DETAILS_ROUTE/$json/$isEditMode"
    this.navigate(route)
}

fun NavController.navigateToAddressDetails(address: Address, isEditMode: Boolean = false) {
    val gson = Gson()
    val json =
        URLEncoder.encode(gson.toJson(address), StandardCharsets.UTF_8.toString())
    val route = "$ADDRESS_DETAILS_ROUTE/$json/$isEditMode"
    this.navigate(route)
}

fun NavController.navigateToOrderConfirmation(
    orderId: String,
    requireConfirmation: Boolean = true
) {
    this.navigate("$ORDER_CONFIRMATION_ROUTE/$orderId/$requireConfirmation") {
        popUpTo(MAIN_GRAPH) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

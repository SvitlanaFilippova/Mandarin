package com.mandarinkafe.mandarin.navigation.extensions

import android.util.Log
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MAIN_GRAPH
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDERS_HISTORY_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_INFO_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SAVED_ADDRESSES_ROUTE
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

fun NavController.navigateToSavedAddresses() {
    this.navigate(SAVED_ADDRESSES_ROUTE)
}

fun NavController.navigateOrdersHistory() {
    this.navigate(ORDERS_HISTORY_ROUTE)
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

fun NavController.navigateToMealDetails(item: CartItem, isEditMode: Boolean) {
    val gson = Gson()
    val json =
        URLEncoder.encode(gson.toJson(item), StandardCharsets.UTF_8.toString())
    val route = "$MEAL_DETAILS_ROUTE/$json/$isEditMode"
    this.navigate(route)
}

fun NavController.navigateToAddressDetails(
    address: Address,
    isEditMode: Boolean = false,
    backTargetRoute: String
) {
    val gson = Gson()
    val json =
        URLEncoder.encode(gson.toJson(address), StandardCharsets.UTF_8.toString())
    val route = "$ADDRESS_DETAILS_ROUTE/$json/$isEditMode/$backTargetRoute"
    this.navigate(route)
}

fun NavController.navigateToOrderInfo(
    orderId: String,
    requireConfirmation: Boolean = true
) {
    this.navigate("$ORDER_INFO_ROUTE/$orderId/$requireConfirmation") {
        popUpTo(MAIN_GRAPH) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavController.tryGetBackStackEntry(route: String): NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (e: IllegalArgumentException) {
        Log.d("Error tryGetBackStackEntry", "error: $e")
        null // экрана в стеке нет
    }
}
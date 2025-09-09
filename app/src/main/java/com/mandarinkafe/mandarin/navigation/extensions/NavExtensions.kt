package com.mandarinkafe.mandarin.navigation.extensions

import android.util.Base64
import android.util.Log
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.navigation.NavConstants.ABOUT_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.CONTACTS_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_IS_EDIT_MODE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_MEAL_ID
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_MEAL_JSON
import com.mandarinkafe.mandarin.navigation.NavConstants.LEGAL_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDERS_HISTORY_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_INFO_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SAVED_ADDRESSES_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.util.Constants.SNACKBAR_MESSAGE_KEY
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun NavController.navigateToSearchScreen(focusInput: Boolean) {
    this.navigate("$SEARCH_SCREEN_ROUTE?focusInput=$focusInput") {
        launchSingleTop = true
        popUpTo(MENU_SCREEN_ROUTE) {
            saveState = true
        }
    }
}

fun NavController.navigateToMenu() {
    this.navigate(MENU_SCREEN_ROUTE)
}

fun NavController.navigateToCart(snackbarMessage: String? = null) {
    this.navigate(CART_SCREEN_ROUTE) {
        launchSingleTop = true
    }
    this.currentBackStackEntry
        ?.savedStateHandle
        ?.set(SNACKBAR_MESSAGE_KEY, snackbarMessage)
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

fun NavController.navigateToLegalScreen() {
    this.navigate(LEGAL_SCREEN_ROUTE)
}

fun NavController.navigateToDeliveryScreen() {
    this.navigate(DELIVERY_SCREEN_ROUTE)
}

fun NavController.navigateToContactsScreen() {
    this.navigate(CONTACTS_SCREEN_ROUTE)
}

fun NavController.navigateToAddress(address: Address? = null, returnToRoute: String) {
    val encodedReturnRoute = URLEncoder.encode(returnToRoute, StandardCharsets.UTF_8.toString())

    if (address == null) {
        // Новый адрес → карта
        val emptyAddress = ""
        navigate("$ADDRESS_SCREEN_ROUTE/$emptyAddress/$encodedReturnRoute")
    } else {
        // Есть уже выбранный адрес
        val gson = Gson()
        val json = gson.toJson(address)
        val encoded = Base64.encodeToString(json.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
        navigate("$ADDRESS_SCREEN_ROUTE/$encoded/$encodedReturnRoute")
    }
}

fun NavController.navigateToAddressDetails(
    address: Address,
    isEditMode: Boolean = false,
    returnToRoute: String
) {
    val gson = Gson()
    val json = gson.toJson(address)
    val encoded = Base64.encodeToString(json.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
    val encodedReturn = URLEncoder.encode(returnToRoute, StandardCharsets.UTF_8.toString())

    val route = "$ADDRESS_DETAILS_ROUTE/$encoded/$isEditMode/$encodedReturn"
    navigate(route)
}

fun NavController.navigateToMealDetails(
    item: CartItem? = null,
    mealId: String? = null,
    isEditMode: Boolean = false
) {
    val gson = Gson()

    val json = item?.let { gson.toJson(it) }
    val encoded = json?.let {
        Base64.encodeToString(it.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
    }

    val itemParam = encoded ?: "null"
    val mealIdParam = mealId ?: "null"

    val route = "$MEAL_DETAILS_ROUTE?" +
            "$KEY_MEAL_JSON=$itemParam&" +
            "$KEY_MEAL_ID=$mealIdParam&" +
            "$KEY_IS_EDIT_MODE=$isEditMode"

    navigate(route)
}

fun NavController.navigateToOrderInfo(
    orderId: String,
    fromOrderCreation: Boolean = false,
) {
    navigate("$ORDER_INFO_ROUTE/$orderId/$fromOrderCreation") {
        if (fromOrderCreation) {
            popUpTo(CART_SCREEN_ROUTE) { inclusive = true }
        }
        launchSingleTop = true
    }
}

fun NavController.navigateToAboutScreen() {
    this.navigate(ABOUT_SCREEN_ROUTE)
}

fun NavController.tryGetBackStackEntry(route: String): NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (e: IllegalArgumentException) {
        Log.d("Error tryGetBackStackEntry", "error: $e")
        null // экрана в стеке нет
    }
}


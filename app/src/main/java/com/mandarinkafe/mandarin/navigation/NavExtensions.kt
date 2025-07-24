package com.mandarinkafe.mandarin.navigation

import androidx.navigation.NavController
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_MAP_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_TEXT_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE
import com.yandex.mapkit.geometry.Geometry
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

fun NavController.navigateToOrder() {
    this.navigate(ORDER_SCREEN_ROUTE)
}

fun NavController.navigateToAddressText(query: String = "", geometry: Geometry) {
    val gson = Gson()
    val jsonGeometry = URLEncoder.encode(gson.toJson(geometry), StandardCharsets.UTF_8.toString())
    val queryEncoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
    val route = "$ADDRESS_TEXT_ROUTE/$queryEncoded/$jsonGeometry"
    this.navigate(route)
}

fun NavController.navigateToAddress(address: UiAddress? = null, isEditMode: Boolean = false) {
    //TODO дописать возможность передачи аргументов для редактирования существующего адреса
    this.navigate(ADDRESS_MAP_SCREEN_ROUTE)
}

fun NavController.navigateToMealDetails(meal: CustomizedMeal, isEditMode: Boolean) {
    val gson = Gson()
    val json =
        URLEncoder.encode(gson.toJson(meal), StandardCharsets.UTF_8.toString())
    val route = "$MEAL_DETAILS_ROUTE/$json/$isEditMode"
    this.navigate(route)
}

fun NavController.navigateToAddressDetails(address: UiAddress, isEditMode: Boolean = false) {
    val gson = Gson()
    val json =
        URLEncoder.encode(gson.toJson(address), StandardCharsets.UTF_8.toString())
    val route = "$ADDRESS_DETAILS_ROUTE/$json/$isEditMode"
    this.navigate(route)
}

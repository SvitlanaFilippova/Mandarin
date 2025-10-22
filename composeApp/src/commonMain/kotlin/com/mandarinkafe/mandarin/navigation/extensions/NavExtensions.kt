package com.mandarinkafe.mandarin.navigation.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.NavConstants.ABOUT_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.CONTACTS_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_FROM_ORDER_CREATION
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_IS_EDIT_MODE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_MEAL_ID
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_MEAL_JSON
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_ORDER_ID
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_SNACKBAR_MESSAGE
import com.mandarinkafe.mandarin.navigation.NavConstants.LEGAL_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDERS_HISTORY_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_INFO_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SAVED_ADDRESSES_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE
import kotlinx.serialization.json.Json
import net.thauvin.erik.urlencoder.UrlEncoderUtil

fun NavController.navigateToSearchScreen(focusInput: Boolean) {
    val route = buildString {
        append(SEARCH_SCREEN_ROUTE)
        append("?${NavConstants.KEY_FOCUS_INPUT}=$focusInput")
    }
    navigate(route)
}

fun NavController.navigateToMenu() {
    navigate(MENU_SCREEN_ROUTE)
}

fun NavController.navigateToCart(snackbarMessage: String? = null) {
    val encodedMessage = snackbarMessage?.let { UrlEncoderUtil.encode(it) }
    val route = if (encodedMessage != null)
        "$CART_SCREEN_ROUTE?$KEY_SNACKBAR_MESSAGE=$encodedMessage"
    else
        CART_SCREEN_ROUTE
    navigate(route)
}

fun NavController.navigateToSavedAddresses() = navigate(SAVED_ADDRESSES_ROUTE)

fun NavController.navigateOrdersHistory() = navigate(ORDERS_HISTORY_ROUTE)

fun NavController.navigateToOrder() = navigate(ORDER_SCREEN_ROUTE)

fun NavController.navigateToLegalScreen() = navigate(LEGAL_SCREEN_ROUTE)

fun NavController.navigateToDeliveryScreen() = navigate(DELIVERY_SCREEN_ROUTE)

fun NavController.navigateToContactsScreen() = navigate(CONTACTS_SCREEN_ROUTE)

fun NavController.navigateToAboutScreen() = navigate(ABOUT_SCREEN_ROUTE)

fun NavController.navigateToAddress(address: Address? = null, returnToRoute: String) {
    val json = address?.let { Json.encodeToString(it) }
    val encodedJson = json?.let { UrlEncoderUtil.encode(it) }
    val encodedReturnRoute = UrlEncoderUtil.encode(returnToRoute)

    val route = buildString {
        append(ADDRESS_SCREEN_ROUTE)
        append("?${NavConstants.KEY_RETURN_TO_ROUTE}=$encodedReturnRoute")
        if (encodedJson != null) append("&${NavConstants.KEY_ADDRESS_JSON}=$encodedJson")
    }
    navigate(route)
}


fun NavController.navigateToAddressDetails(
    address: Address,
    isEditMode: Boolean = false,
    returnToRoute: String
) {
    val encodedAddress = UrlEncoderUtil.encode(Json.encodeToString(address))
    val encodedReturn = UrlEncoderUtil.encode(returnToRoute)

    val route = "$ADDRESS_DETAILS_ROUTE?" +
            "${NavConstants.KEY_IS_EDIT_MODE}=$isEditMode&" +
            "${NavConstants.KEY_ADDRESS_JSON}=$encodedAddress&" +
            "${NavConstants.KEY_RETURN_TO_ROUTE}=$encodedReturn"

    navigate(route)
}

fun NavController.navigateToMealDetails(
    item: CartItem? = null,
    mealId: String? = null,
    isEditMode: Boolean = false
) {
    val json = item?.let { Json.encodeToString(it) }

    val itemParam = json ?: "null"
    val mealIdParam = mealId ?: "null"
    val encodedItem = UrlEncoderUtil.encode(itemParam)
    val encodedMealId = UrlEncoderUtil.encode(mealIdParam)
    val route = "$MEAL_DETAILS_ROUTE?" +
            "$KEY_MEAL_JSON=$encodedItem&" +
            "$KEY_MEAL_ID=$encodedMealId&" +
            "$KEY_IS_EDIT_MODE=$isEditMode"

    navigate(route)
}

fun NavController.navigateToOrderInfo(
    orderId: String,
    fromOrderCreation: Boolean = false,
) {
    val encodedOrderId = UrlEncoderUtil.encode(orderId)
    val route = "$ORDER_INFO_ROUTE?$KEY_ORDER_ID=$encodedOrderId&$KEY_FROM_ORDER_CREATION=$fromOrderCreation"

    if (fromOrderCreation) {
        navigate(
            route,
            navOptions {
                launchSingleTop = true
                popUpTo(CART_SCREEN_ROUTE) {
                    inclusive = true
                }
            }
        )
    } else {
        navigate(route)
    }
}




package com.mandarinkafe.mandarin.navigation.extensions

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
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo
import net.thauvin.erik.urlencoder.UrlEncoderUtil

fun Navigator.navigateToSearchScreen(focusInput: Boolean) {
    val route = buildString {
        append(SEARCH_SCREEN_ROUTE)
        append("?${NavConstants.KEY_FOCUS_INPUT}=$focusInput")
    }
    navigate(route)
}

fun Navigator.navigateToMenu() {
    this.navigate(MENU_SCREEN_ROUTE)
}

fun Navigator.navigateToCart(snackbarMessage: String? = null) {
    val encodedMessage = snackbarMessage?.let { UrlEncoderUtil.encode(it) }
    val route = if (encodedMessage != null)
        "$CART_SCREEN_ROUTE?$KEY_SNACKBAR_MESSAGE=$encodedMessage"
    else
        CART_SCREEN_ROUTE
    navigate(route)
}

fun Navigator.navigateToSavedAddresses() = navigate(SAVED_ADDRESSES_ROUTE)

fun Navigator.navigateOrdersHistory() = navigate(ORDERS_HISTORY_ROUTE)

fun Navigator.navigateToOrder() = navigate(ORDER_SCREEN_ROUTE)

fun Navigator.navigateToLegalScreen() = navigate(LEGAL_SCREEN_ROUTE)

fun Navigator.navigateToDeliveryScreen() = navigate(DELIVERY_SCREEN_ROUTE)

fun Navigator.navigateToContactsScreen() = navigate(CONTACTS_SCREEN_ROUTE)

fun Navigator.navigateToAboutScreen() = navigate(ABOUT_SCREEN_ROUTE)

fun Navigator.navigateToAddress(address: Address? = null, returnToRoute: String) {
    val json = address?.let { Json.encodeToString(it) }
    val encodedJson = json?.let { UrlEncoderUtil.encode(it) }
    val encodedReturnRoute = UrlEncoderUtil.encode(returnToRoute)

    val route = buildString {
        append(ADDRESS_SCREEN_ROUTE)
        append("?returnTo=$encodedReturnRoute")
        if (encodedJson != null) append("&address=$encodedJson")
    }
    navigate(route)
}


fun Navigator.navigateToAddressDetails(
    address: Address,
    isEditMode: Boolean = false,
    returnToRoute: String
) {
    val encodedAddress = UrlEncoderUtil.encode(Json.encodeToString(address))
    val encodedReturn = UrlEncoderUtil.encode(returnToRoute)

    val route = "$ADDRESS_DETAILS_ROUTE?" +
            "address=$encodedAddress&" +
            "isEditMode=$isEditMode&" +
            "returnTo=$encodedReturn"

    navigate(route)
}

fun Navigator.navigateToMealDetails(
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

fun Navigator.navigateToOrderInfo(
    orderId: String,
    fromOrderCreation: Boolean = false,
) {
    val encodedOrderId = UrlEncoderUtil.encode(orderId)
    val route = "$ORDER_INFO_ROUTE?$KEY_ORDER_ID=$encodedOrderId&$KEY_FROM_ORDER_CREATION=$fromOrderCreation"

    if (fromOrderCreation) {
        navigate(
            route,
            options = NavOptions(
                launchSingleTop = true,
                popUpTo = PopUpTo(
                    route = CART_SCREEN_ROUTE,
                    inclusive = true
                )
            )
        )
    } else {
        navigate(route)
    }
}




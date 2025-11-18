package com.mandarinkafe.mandarin.navigation.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.mandarinkafe.mandarin.core.di.ServiceLocator
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.auth.domain.impl.AuthStateChecker
import com.mandarinkafe.mandarin.navigation.MealDetailsNavParams
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
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import net.thauvin.erik.urlencoder.UrlEncoderUtil


fun NavController.navigateToMenu() {
    navigate(MENU_SCREEN_ROUTE)
}

fun NavController.navigateToCart(snackbarMessage: String? = null) {
    val encodedMessage = snackbarMessage?.let { UrlEncoderUtil.encode(it) }
    val route = if (encodedMessage != null) {
        "$CART_SCREEN_ROUTE?$KEY_SNACKBAR_MESSAGE=$encodedMessage"
    } else {
        CART_SCREEN_ROUTE
    }
    navigate(route)
}

fun NavController.navigateToSavedAddresses() = navigate(SAVED_ADDRESSES_ROUTE)

fun NavController.navigateOrdersHistory() = navigate(ORDERS_HISTORY_ROUTE)

fun NavController.navigateToOrder() = navigateWithAuthCheck(ORDER_SCREEN_ROUTE)

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
    returnToRoute: String,
) {
    val encodedAddress = UrlEncoderUtil.encode(Json.encodeToString(address))
    val encodedReturn = UrlEncoderUtil.encode(returnToRoute)

    val route = "$ADDRESS_DETAILS_ROUTE?" +
            "${KEY_IS_EDIT_MODE}=$isEditMode&" +
            "${NavConstants.KEY_ADDRESS_JSON}=$encodedAddress&" +
            "${NavConstants.KEY_RETURN_TO_ROUTE}=$encodedReturn"

    navigate(route)
}

fun NavController.navigateToMealDetails(
    item: CartItem? = null,
    mealId: String? = null,
    isEditMode: Boolean = false,
) {
    // Передаем только идентификаторы вместо полного объекта
    val params = if (item != null) {
        val meal = item.customizedMeal.meal
        val addsIds = item.customizedMeal.adds.map { it.id }
        val modifierIds = item.customizedMeal.modifiers.associate { group ->
            group.id to group.items.map { it.id }
        }
        MealDetailsNavParams(
            mealId = meal.id,
            addsIds = addsIds,
            modifierIds = modifierIds,
            comment = item.comment,
            cartItemId = if (isEditMode) item.id else null
        )
    } else if (mealId != null) {
        MealDetailsNavParams(mealId = mealId)
    } else {
        return
    }
    
    // Сериализуем в JSON и URL-encode
    val jsonString = Json.encodeToString(params)
    val encodedParams = UrlEncoderUtil.encode(jsonString)
    
    val route = "$MEAL_DETAILS_ROUTE?$KEY_MEAL_ID=$encodedParams&$KEY_IS_EDIT_MODE=$isEditMode"
    navigate(route)
}

fun NavController.navigateToOrderInfo(
    orderId: String,
    fromOrderCreation: Boolean = false,
) {
    val encodedOrderId = UrlEncoderUtil.encode(orderId)
    val route =
        "$ORDER_INFO_ROUTE?$KEY_ORDER_ID=$encodedOrderId&$KEY_FROM_ORDER_CREATION=$fromOrderCreation"

    if (fromOrderCreation) {
        navigate(
            route,
            navOptions {
                popUpTo(CART_SCREEN_ROUTE) {
                    inclusive = true
                }
            }
        )
    } else {
        navigate(route)
    }
}


fun NavController.navigateToAccountScreen() {
    navigateWithAuthCheck(targetRoute = NavConstants.ACCOUNT_ROUTE)
}

fun NavController.navigateToAuthScreen(targetRoute: String? = null) {
    val encodedTarget = targetRoute?.let { UrlEncoderUtil.encode(it) }
    val route = if (encodedTarget != null) {
        "${NavConstants.AUTH_ROUTE}?${NavConstants.KEY_TARGET_ROUTE}=$encodedTarget"
    } else {
        NavConstants.AUTH_ROUTE
    }
    navigate(route)
}

fun NavController.navigateWithAuthCheck(targetRoute: String) {
    val authStateChecker: AuthStateChecker = ServiceLocator.koin.get()
    val isAuthorized = authStateChecker.isAuthorizedFast()

    if (isAuthorized) {
        navigate(targetRoute)
    } else {
        navigateToAuthScreen(targetRoute = targetRoute)
    }
}

fun NavController.tryGetBackStackEntry(route: String): NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (e: IllegalArgumentException) {
        Napier.e("error: $e")
        null
    }
}

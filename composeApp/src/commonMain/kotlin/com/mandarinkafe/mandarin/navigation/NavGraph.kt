package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui.AddressDetailsScreen
import com.mandarinkafe.mandarin.features.address.presentation.ui.screen.AddressMapScreen
import com.mandarinkafe.mandarin.features.cart.presentation.screen.CartScreen
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.presentation.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.AboutScreen
import com.mandarinkafe.mandarin.features.contacts.presentation.screen.ContactsScreen
import com.mandarinkafe.mandarin.features.delivery.presentation.ui.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.LegalScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.MoreMenuScreen
import com.mandarinkafe.mandarin.features.order.presentation.ui.screen.OrderScreen
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen.OrderInfoScreen
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen.OrdersHistoryScreen
import com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.screen.SavedAddressesScreen
import com.mandarinkafe.mandarin.features.search.presentation.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberCartViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberSharedViewModel
import com.mandarinkafe.mandarin.splash.presentation.SplashScreen
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import io.ktor.http.decodeURLPart

@Composable
fun NavGraph(navController: NavHostController) {
    val cartViewModel = rememberCartViewModel()
    val sharedViewModel = rememberSharedViewModel()

    NavHost(
        navController = navController,
        startDestination = NavConstants.SPLASH_SCREEN_ROUTE
    ) {
        // --- SPLASH ---
        composable(NavConstants.SPLASH_SCREEN_ROUTE) {
            SplashScreen()
        }

        // --- ОСНОВНЫЕ ЭКРАНЫ (доступны из Bottom Navigation)---
        composable(NavConstants.MENU_SCREEN_ROUTE) {
            MenuScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(NavConstants.FAVORITES_SCREEN_ROUTE) {
            FavoritesScreen(
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel
            )
        }

        composable(
            route = "${NavConstants.CART_SCREEN_ROUTE}?${NavConstants.KEY_SNACKBAR_MESSAGE}={${NavConstants.KEY_SNACKBAR_MESSAGE}}"
        ) { backStackEntry ->
            val snackbarMessage = backStackEntry.getStringArgument(NavConstants.KEY_SNACKBAR_MESSAGE)

            CartScreen(
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel,
                navController = navController,
                snackbarMessage = snackbarMessage?.decodeURLPart(),
            )
        }

        composable(NavConstants.MORE_MENU_SCREEN_ROUTE) {
            MoreMenuScreen(navController = navController)
        }


        // --- ОСТАЛЬНЫЕ ЭКРАНЫ ---

        composable(
            route = "${NavConstants.SEARCH_SCREEN_ROUTE}?${NavConstants.KEY_FOCUS_INPUT}={${NavConstants.KEY_FOCUS_INPUT}}"
        ) { backStackEntry ->
            val focusInput = backStackEntry.getBooleanArgument(NavConstants.KEY_FOCUS_INPUT, defaultValue = false)
            SearchScreen(
                focusSearchBarInput = focusInput,
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavConstants.ORDERS_HISTORY_ROUTE) {
            OrdersHistoryScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        composable(NavConstants.SAVED_ADDRESSES_ROUTE) {
            SavedAddressesScreen(navController = navController)
        }

        composable(NavConstants.ABOUT_SCREEN_ROUTE) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavConstants.LEGAL_SCREEN_ROUTE) {
            LegalScreen(
                onBackClick = { navController.popBackStack() },
                onSharedEvent = sharedViewModel::onEvent
            )
        }

        composable(NavConstants.DELIVERY_SCREEN_ROUTE) {
            DeliveryScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavConstants.CONTACTS_SCREEN_ROUTE) {
            ContactsScreen(onBackClick = { navController.popBackStack() })
        }

        // --- MEAL DETAILS (BottomSheet) ---
        this.platformMealDetailsRoute(
        ) { backStackEntry ->
            val isEditMode = backStackEntry.getBooleanArgument(NavConstants.KEY_IS_EDIT_MODE, defaultValue = false)
            val mealJson = backStackEntry.getStringArgument(NavConstants.KEY_MEAL_JSON)?.decodeURLPart()
            val mealId = backStackEntry.getStringArgument(NavConstants.KEY_MEAL_ID)?.decodeURLPart()

            val initItem = mealJson?.let {
                runCatching { Json.decodeFromString<CartItem>(it) }.getOrNull()
            }

            MealDetailsBottomSheet(
                sharedViewModel = sharedViewModel,
                navController = navController,
                mealId = if (mealId != "null") mealId else null,
                initItem = initItem,
                isEditMode = isEditMode,
            )
        }


        composable(
            route = "${NavConstants.ADDRESS_SCREEN_ROUTE}?" +
                    "${NavConstants.KEY_ADDRESS_JSON}={${NavConstants.KEY_ADDRESS_JSON}}&" +
                    "${NavConstants.KEY_RETURN_TO_ROUTE}={${NavConstants.KEY_RETURN_TO_ROUTE}}"
        ) { backStackEntry ->
            val addressJson = backStackEntry.getStringArgument(NavConstants.KEY_ADDRESS_JSON)?.decodeURLPart()
            val returnToRoute = backStackEntry.getStringArgument(NavConstants.KEY_RETURN_TO_ROUTE)
                ?.decodeURLPart()
                ?: ""

            val address = addressJson?.let {
                runCatching { Json.decodeFromString<Address>(it) }.getOrNull()
            }

            AddressMapScreen(
                navController = navController,
                initAddress = address,
                returnToRoute = returnToRoute
            )
        }


        composable(
            route = "${NavConstants.ADDRESS_DETAILS_ROUTE}?" +
                    "${NavConstants.KEY_IS_EDIT_MODE}={${NavConstants.KEY_IS_EDIT_MODE}}&" +
                    "${NavConstants.KEY_ADDRESS_JSON}={${NavConstants.KEY_ADDRESS_JSON}}&" +
                    "${NavConstants.KEY_RETURN_TO_ROUTE}={${NavConstants.KEY_RETURN_TO_ROUTE}}"
        ) { backStackEntry ->
            val isEditMode = backStackEntry.getBooleanArgument(NavConstants.KEY_IS_EDIT_MODE, defaultValue = false)
            val addressJson = backStackEntry.getStringArgument(NavConstants.KEY_ADDRESS_JSON)?.decodeURLPart()
            val returnToRoute = backStackEntry.getStringArgument(NavConstants.KEY_RETURN_TO_ROUTE)
                ?.decodeURLPart()
                ?: ""

            val address = addressJson?.let {
                runCatching { Json.decodeFromString<Address>(it) }.getOrNull()
            }

            AddressDetailsScreen(
                initAddress = address,
                returnToRoute = returnToRoute,
                isEditMode = isEditMode,
                navController = navController,
                callerEntry = backStackEntry
            )
        }


        composable(NavConstants.ORDER_SCREEN_ROUTE) {
            OrderScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }


        composable(
            route = "${NavConstants.ORDER_INFO_ROUTE}?" +
                    "${NavConstants.KEY_ORDER_ID}={${NavConstants.KEY_ORDER_ID}}&" +
                    "${NavConstants.KEY_FROM_ORDER_CREATION}={${NavConstants.KEY_FROM_ORDER_CREATION}}"
        ) { backStackEntry ->
            val orderId = backStackEntry.getStringArgument(NavConstants.KEY_ORDER_ID)?.decodeURLPart() ?: ""
            val fromOrderCreation = backStackEntry.getBooleanArgument(NavConstants.KEY_FROM_ORDER_CREATION, defaultValue = false)

            OrderInfoScreen(
                orderID = orderId,
                fromOrderCreation = fromOrderCreation,
                sharedViewModel = sharedViewModel,
                navController = navController
            )
        }
    }
}

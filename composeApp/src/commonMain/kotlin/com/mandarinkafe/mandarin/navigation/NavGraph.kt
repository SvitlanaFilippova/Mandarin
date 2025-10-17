package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui.AddressDetailsScreen
import com.mandarinkafe.mandarin.features.address.presentation.ui.screen.AddressMapScreen
import com.mandarinkafe.mandarin.features.cart.presentation.screen.CartScreen
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.presentation.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.AboutScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.ContactsScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.LegalScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.MoreMenuScreen
import com.mandarinkafe.mandarin.features.order.presentation.ui.screen.OrderScreen
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen.OrderInfoScreen
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen.OrdersHistoryScreen
import com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.screen.SavedAddressesScreen
import com.mandarinkafe.mandarin.features.search.presentation.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberCartViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberSharedViewModel
import com.mandarinkafe.mandarin.splash.presentation.SplashScreen
import kotlinx.serialization.json.Json
import io.ktor.http.decodeURLPart
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.rememberNavigator


@Composable
    fun NavGraph() {
    val navigator = rememberNavigator()
    val cartViewModel = rememberCartViewModel()
    val sharedViewModel = rememberSharedViewModel()

    NavHost(
        navigator = navigator,
        initialRoute = NavConstants.SPLASH_SCREEN_ROUTE
    ) {
        // --- SPLASH ---
        scene(NavConstants.SPLASH_SCREEN_ROUTE) {
            SplashScreen()
        }

        // --- MAIN GRAPH (начало) ---
        scene(NavConstants.MENU_SCREEN_ROUTE) {
            MenuScreen(
                navigator = navigator,
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        scene(NavConstants.FAVORITES_SCREEN_ROUTE) {
            FavoritesScreen(
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel
            )
        }

        scene(NavConstants.CART_SCREEN_ROUTE_WITH_ARGS) { backStackEntry ->
            val snackbarMessage = backStackEntry.query<String>(NavConstants.KEY_SNACKBAR_MESSAGE)

            CartScreen(
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel,
                navigator = navigator,
                snackbarMessage = snackbarMessage?.decodeURLPart(),
            )
        }

        scene(NavConstants.MORE_MENU_SCREEN_ROUTE) {
            MoreMenuScreen(navigator = navigator)
        }
        // --- MAIN GRAPH (тут должен быть конец) ---


        // --- ОСТАЛЬНЫЕ ЭКРАНЫ ---
        scene(NavConstants.SEARCH_SCREEN_ROUTE_WITH_ARGS) { backStackEntry ->
            val focusInput = backStackEntry.query<Boolean>(NavConstants.KEY_FOCUS_INPUT) ?: false
            SearchScreen(
                focusSearchBarInput = focusInput,
                cartViewModel = cartViewModel,
                sharedViewModel = sharedViewModel,
                onBackClick = { navigator.goBack() }
            )
        }

        scene(NavConstants.ORDERS_HISTORY_ROUTE) {
            OrdersHistoryScreen(
                navigator = navigator,
                sharedViewModel = sharedViewModel
            )
        }

        scene(NavConstants.SAVED_ADDRESSES_ROUTE) {
            SavedAddressesScreen(navigator = navigator)
        }

        scene(NavConstants.ABOUT_SCREEN_ROUTE) {
            AboutScreen(onBackClick = { navigator.goBack() })
        }

        scene(NavConstants.LEGAL_SCREEN_ROUTE) {
            LegalScreen(
                onBackClick = { navigator.goBack() },
                onSharedEvent = sharedViewModel::onEvent
            )
        }

        scene(NavConstants.DELIVERY_SCREEN_ROUTE) {
            DeliveryScreen(onBackClick = { navigator.goBack() })
        }

        scene(NavConstants.CONTACTS_SCREEN_ROUTE) {
            ContactsScreen(onBackClick = { navigator.goBack() })
        }

        // --- MEAL DETAILS (BottomSheet) ---
        dialog(NavConstants.MEAL_DETAILS_ROUTE_WITH_ARGS) { backStackEntry ->
            val isEditMode = backStackEntry.query<Boolean>(NavConstants.KEY_IS_EDIT_MODE) ?: false
            val mealJson = backStackEntry.query<String>(NavConstants.KEY_MEAL_JSON)?.decodeURLPart()
            val mealId = backStackEntry.query<String>(NavConstants.KEY_MEAL_ID)?.decodeURLPart()

            val initItem = mealJson?.let {
                runCatching { Json.decodeFromString<CartItem>(it) }.getOrNull()
            }

            MealDetailsBottomSheet(
                sharedViewModel = sharedViewModel,
                mealId = if (mealId != "null") mealId else null,
                initItem = initItem,
                isEditMode = isEditMode,
                onClose = { navigator.goBack() },
            )
        }


        scene(NavConstants.ADDRESS_SCREEN_ROUTE_WITH_ARGS) { backStackEntry ->
            val addressJson = backStackEntry.query<String>(NavConstants.KEY_ADDRESS_JSON)?.decodeURLPart()
            val returnToRoute = backStackEntry.query<String>(NavConstants.KEY_RETURN_TO_ROUTE)
                ?.decodeURLPart()
                ?: ""

            val address = addressJson?.let {
                runCatching { Json.decodeFromString<Address>(it) }.getOrNull()
            }

            AddressMapScreen(
                navigator = navigator,
                initAddress = address,
                returnToRoute = returnToRoute
            )
        }


        scene(NavConstants.ADDRESS_DETAILS_ROUTE_WITH_ARGS) { backStackEntry ->
            val isEditMode = backStackEntry.query<Boolean>(NavConstants.KEY_IS_EDIT_MODE) ?: false
            val addressJson = backStackEntry.query<String>(NavConstants.KEY_ADDRESS_JSON)?.decodeURLPart()
            val returnToRoute = backStackEntry.query<String>(NavConstants.KEY_RETURN_TO_ROUTE)
                ?.decodeURLPart()
                ?: ""

            val address = addressJson?.let {
                runCatching { Json.decodeFromString<Address>(it) }.getOrNull()
            }

            AddressDetailsScreen(
                initAddress = address,
                returnToRoute = returnToRoute,
                isEditMode = isEditMode,
                navigator = navigator,
                callerEntry = backStackEntry
            )
        }


        scene(NavConstants.ORDER_SCREEN_ROUTE) {
            OrderScreen(
                navigator = navigator,
                sharedViewModel = sharedViewModel
            )
        }


        scene(NavConstants.ORDER_INFO_ROUTE_WITH_ARGS) { backStackEntry ->
            val orderId = backStackEntry.query<String>(NavConstants.KEY_ORDER_ID)?.decodeURLPart() ?: ""
            val fromOrderCreation = backStackEntry.query<Boolean>(NavConstants.KEY_FROM_ORDER_CREATION) ?: false

            OrderInfoScreen(
                orderID = orderId,
                fromOrderCreation = fromOrderCreation,
                sharedViewModel = sharedViewModel,
                navigator = navigator
            )
        }
    }
}


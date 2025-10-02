package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.screen.AddressMapScreen
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui.AddressDetailsScreen
import com.mandarinkafe.mandarin.features.cart.presentation.screen.CartScreen
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.presentation.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.AboutScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.ContactsScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.LegalScreen
import com.mandarinkafe.mandarin.features.more.presentation.ui.screen.MoreMenuScreen
import com.mandarinkafe.mandarin.features.order.presentation.ui.screen.OrderScreen
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen.OrderInfoScreen
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen.OrdersHistoryScreen
import com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.screen.SavedAddressesScreen
import com.mandarinkafe.mandarin.features.search.presentation.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_ADDRESS_JSON
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_IS_EDIT_MODE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_RETURN_TO_ROUTE
import com.mandarinkafe.mandarin.navigation.extensions.boolNavArg
import com.mandarinkafe.mandarin.navigation.extensions.decodeJsonArg
import com.mandarinkafe.mandarin.navigation.extensions.stringNavArg
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.splash.presentation.SplashScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun NavGraph(navHostController: NavHostController) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navHostController.navigatorProvider.addNavigator(bottomSheetNavigator)

    val cartViewModel: CartViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val orderViewModel: OrderViewModel = hiltViewModel()
    val gson = remember { Gson() }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator
    ) {
        NavHost(
            navController = navHostController,
            startDestination = NavConstants.SPLASH_SCREEN_ROUTE
        ) {
            composable(NavConstants.SPLASH_SCREEN_ROUTE) {
                SplashScreen()
            }

            // экраны, доступные через BottomNavigation:
            navigation(
                startDestination = NavConstants.MENU_SCREEN_ROUTE,
                route = NavConstants.MAIN_GRAPH
            ) {
                composable(NavConstants.MENU_SCREEN_ROUTE) {
                    MenuScreen(
                        navController = navHostController,
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

                composable(NavConstants.CART_SCREEN_ROUTE) {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        sharedViewModel = sharedViewModel,
                        navController = navHostController
                    )
                }

                composable(NavConstants.MORE_MENU_SCREEN_ROUTE) {
                    MoreMenuScreen(
                        navController = navHostController,
                    )
                }
            }

            // Остальные экраны:
            composable(
                route = NavConstants.SEARCH_SCREEN_ROUTE_WITH_ARGS,
                arguments = listOf(
                    navArgument(NavConstants.KEY_FOCUS_INPUT) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val focusInput =
                    backStackEntry.arguments?.getBoolean(NavConstants.KEY_FOCUS_INPUT) == true
                SearchScreen(
                    focusSearchBarInput = focusInput,
                    cartViewModel = cartViewModel,
                    sharedViewModel = sharedViewModel,
                    onBackClick = { navHostController.popBackStack() }
                )
            }

            composable(NavConstants.ORDERS_HISTORY_ROUTE) {
                OrdersHistoryScreen(
                    navController = navHostController,
                    sharedViewModel = sharedViewModel,
                )
            }

            composable(NavConstants.SAVED_ADDRESSES_ROUTE) {
                SavedAddressesScreen(
                    navController = navHostController,
                )
            }

            composable(NavConstants.ABOUT_SCREEN_ROUTE) {
                AboutScreen(onBackClick = { navHostController.popBackStack() })
            }

            composable(NavConstants.LEGAL_SCREEN_ROUTE) {
                LegalScreen(
                    onBackClick = { navHostController.popBackStack() },
                    onSharedEvent = sharedViewModel::onEvent
                )
            }

            composable(NavConstants.DELIVERY_SCREEN_ROUTE) {
                DeliveryScreen(onBackClick = { navHostController.popBackStack() })
            }

            composable(NavConstants.CONTACTS_SCREEN_ROUTE) {
                ContactsScreen(onBackClick = { navHostController.popBackStack() })
            }

            bottomSheet(
                route = NavConstants.MEAL_DETAILS_ROUTE_WITH_ARGS,
                arguments = listOf(
                    stringNavArg(NavConstants.KEY_MEAL_JSON, nullable = true),
                    stringNavArg(NavConstants.KEY_MEAL_ID, nullable = true),
                    boolNavArg(KEY_IS_EDIT_MODE)
                )
            ) { backStackEntry ->
                val isEditMode =
                    backStackEntry.arguments?.getBoolean(KEY_IS_EDIT_MODE) == true
                val item =
                    backStackEntry.decodeJsonArg<CartItem>(NavConstants.KEY_MEAL_JSON, gson)
                val mealId = backStackEntry.arguments?.getString(NavConstants.KEY_MEAL_ID)

                MealDetailsBottomSheet(
                    sharedViewModel = sharedViewModel,
                    mealId = if (mealId != "null") mealId else null,
                    initItem = item,
                    isEditMode = isEditMode,
                    onClose = { navHostController.popBackStack() },
                )
            }

            composable(
                route = NavConstants.ADDRESS_SCREEN_ROUTE_WITH_ARGS,
                arguments = listOf(
                    stringNavArg(KEY_ADDRESS_JSON),
                    stringNavArg(KEY_RETURN_TO_ROUTE)
                )
            ) { backStackEntry ->
                val address =
                    backStackEntry.decodeJsonArg<Address?>(KEY_ADDRESS_JSON, gson)
                val returnToRoute = backStackEntry.arguments
                    ?.getString(KEY_RETURN_TO_ROUTE)
                    ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    ?: ""

                AddressMapScreen(
                    navController = navHostController,
                    initAddress = address,
                    returnToRoute = returnToRoute
                )
            }

            composable(
                route = NavConstants.ADDRESS_DETAILS_ROUTE_WITH_ARGS,
                arguments = listOf(
                    stringNavArg(KEY_ADDRESS_JSON),
                    boolNavArg(KEY_IS_EDIT_MODE),
                    stringNavArg(KEY_RETURN_TO_ROUTE),
                )
            ) { backStackEntry ->
                val isEditMode =
                    backStackEntry.arguments?.getBoolean(KEY_IS_EDIT_MODE) == true
                val address =
                    backStackEntry.decodeJsonArg<Address>(KEY_ADDRESS_JSON, gson)
                val returnToRoute = backStackEntry.arguments?.getString(KEY_RETURN_TO_ROUTE)
                    ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                    ?: ""

                AddressDetailsScreen(
                    initAddress = address,
                    returnToRoute = returnToRoute,
                    isEditMode = isEditMode,
                    navController = navHostController,
                    callerEntry = backStackEntry
                )
            }

            composable(NavConstants.ORDER_SCREEN_ROUTE) {
                OrderScreen(
                    navController = navHostController,
                    orderViewModel = orderViewModel,
                    sharedViewModel = sharedViewModel
                )
            }

            composable(
                route = NavConstants.ORDER_INFO_ROUTE_WITH_ARGS,
                arguments = listOf(
                    navArgument(NavConstants.KEY_ORDER_ID) { type = NavType.StringType },
                    navArgument(NavConstants.KEY_FROM_ORDER_CREATION) { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString(NavConstants.KEY_ORDER_ID) ?: ""
                val fromOrderCreation =
                    backStackEntry.arguments?.getBoolean(NavConstants.KEY_FROM_ORDER_CREATION) == true

                OrderInfoScreen(
                    orderID = orderId,
                    fromOrderCreation = fromOrderCreation,
                    sharedViewModel = sharedViewModel,
                    navController = navHostController,
                )
            }
        }
    }
}
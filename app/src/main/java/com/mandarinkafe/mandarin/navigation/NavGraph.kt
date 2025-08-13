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
import com.mandarinkafe.mandarin.features.address.savedadresses.presentation.ui.screen.SavedAddressesScreen
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
import com.mandarinkafe.mandarin.features.search.presentation.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.navigation.extensions.boolNavArg
import com.mandarinkafe.mandarin.navigation.extensions.decodeJsonArg
import com.mandarinkafe.mandarin.navigation.extensions.stringNavArg
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.splash.presentation.SplashScreen

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

            navigation(
                startDestination = NavConstants.MENU_SCREEN_ROUTE,
                route = NavConstants.MAIN_GRAPH
            ) {
                // экраны, доступные через BottomNavigation:
                composable(NavConstants.MENU_SCREEN_ROUTE) {
                    MenuScreen(
                        navController = navHostController,
                        cartViewModel = cartViewModel,
                        sharedViewModel = sharedViewModel
                    )
                }
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
            }

            bottomSheet(
                route = NavConstants.MEAL_DETAILS_ROUTE_WITH_ARGS,
                arguments = listOf(
                    stringNavArg(NavConstants.KEY_MEAL_JSON, nullable = true),
                    stringNavArg(NavConstants.KEY_MEAL_ID, nullable = true),
                    boolNavArg(NavConstants.KEY_IS_EDIT_MODE)
                )
            ) { backStackEntry ->
                val isEditMode =
                    backStackEntry.arguments?.getBoolean(NavConstants.KEY_IS_EDIT_MODE) == true
                val item =
                    backStackEntry.decodeJsonArg<CartItem>(NavConstants.KEY_MEAL_JSON, gson)
                val mealId = backStackEntry.arguments?.getString(NavConstants.KEY_MEAL_ID)

                MealDetailsBottomSheet(
                    sharedViewModel = sharedViewModel,
                    cartViewModel = cartViewModel,
                    mealId = if (mealId != "null") mealId else null,
                    initItem = item,
                    isEditMode = isEditMode,
                    onClose = { navHostController.popBackStack() },
                )
            }

            composable(route = NavConstants.ADDRESS_SCREEN_ROUTE) {
                AddressMapScreen(navController = navHostController, initAddress = null)
            }
            composable(
                route = NavConstants.ADDRESS_SCREEN_ROUTE_WITH_ARGS,
                arguments = listOf(
                    stringNavArg(NavConstants.KEY_ADDRESS_JSON)
                )
            ) { backStackEntry ->
                val address =
                    backStackEntry.decodeJsonArg<Address?>(NavConstants.KEY_ADDRESS_JSON, gson)
                AddressMapScreen(navController = navHostController, initAddress = address)
            }

            composable(
                route = NavConstants.ADDRESS_DETAILS_ROUTE_WITH_ARGS,
                arguments = listOf(
                    stringNavArg(NavConstants.KEY_ADDRESS_JSON),
                    boolNavArg(NavConstants.KEY_IS_EDIT_MODE),
                    stringNavArg(NavConstants.KEY_BACK_TARGET),
                )
            ) { backStackEntry ->
                val isEditMode =
                    backStackEntry.arguments?.getBoolean(NavConstants.KEY_IS_EDIT_MODE) == true
                val address =
                    backStackEntry.decodeJsonArg<Address>(NavConstants.KEY_ADDRESS_JSON, gson)
                val backTarget =
                    backStackEntry.arguments?.getString(NavConstants.KEY_BACK_TARGET) ?: ""

                AddressDetailsScreen(
                    isEditMode = isEditMode,
                    initAddress = address,
                    navController = navHostController,
                    backTarget = backTarget
                )
            }
            composable(NavConstants.ORDER_SCREEN_ROUTE) {
                OrderScreen(
                    navController = navHostController,
                    orderViewModel = orderViewModel
                )
            }

            composable(
                route = NavConstants.ORDER_INFO_ROUTE_WITH_ARGS,
                arguments = listOf(
                    navArgument(NavConstants.KEY_ORDER_ID) {
                        type = NavType.StringType
                    },
                    boolNavArg(NavConstants.KEY_REQUIRE_CONFIRMATION)
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString(NavConstants.KEY_ORDER_ID) ?: ""
                val requireConfirmation =
                    backStackEntry.arguments?.getBoolean(NavConstants.KEY_REQUIRE_CONFIRMATION) == true
                OrderInfoScreen(
                    orderID = orderId,
                    requireConfirmation = requireConfirmation,
                    navController = navHostController,
                    sharedViewModel = sharedViewModel,
                )
            }

            composable(NavConstants.MORE_MENU_SCREEN_ROUTE) {
                MoreMenuScreen(
                    navController = navHostController,
                )
            }

            composable(NavConstants.ORDERS_HISTORY_ROUTE) {
                OrdersHistoryScreen(
                    navController = navHostController,
                )
            }
            composable(NavConstants.SAVED_ADDRESSES_ROUTE) {
                SavedAddressesScreen(
                    navController = navHostController,
                )
            }

            composable(NavConstants.ABOUT_SCREEN_ROUTE) {
                AboutScreen()
            }
            composable(NavConstants.LEGAL_SCREEN_ROUTE) {
                LegalScreen()
            }
            composable(NavConstants.DELIVERY_SCREEN_ROUTE) {
                DeliveryScreen()
            }
            composable(NavConstants.CONTACTS_SCREEN_ROUTE) {
                ContactsScreen()
            }

        }
    }
}
package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.screen.AddressMapScreen
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui.AddressDetailsScreen
import com.mandarinkafe.mandarin.features.cart.presentation.screen.CartScreen
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.delivery.presentation.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.presentation.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.order.presentation.ui.screen.OrderScreen
import com.mandarinkafe.mandarin.features.search.presentation.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_DETAILS_ROUTE_WITH_ARGS
import com.mandarinkafe.mandarin.navigation.NavConstants.ADDRESS_SCREEN_ROUTE_WITH_ARGS
import com.mandarinkafe.mandarin.navigation.NavConstants.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_ADDRESS_JSON
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_FOCUS_INPUT
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_IS_EDIT_MODE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_MEAL_JSON
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE_WITH_ARGS
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE_WITH_ARGS
import com.mandarinkafe.mandarin.navigation.NavConstants.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.extensions.boolNavArg
import com.mandarinkafe.mandarin.navigation.extensions.decodeJsonArg
import com.mandarinkafe.mandarin.navigation.extensions.jsonNavArg
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.splash.presentation.SplashScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun NavGraph(navHostController: NavHostController) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navHostController.navigatorProvider.addNavigator(bottomSheetNavigator)

    val cartViewModel: CartViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val gson = remember { Gson() }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator
    ) {
        NavHost(
            navController = navHostController,
            startDestination = SPLASH_SCREEN_ROUTE
        ) {
            composable(CART_SCREEN_ROUTE) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    sharedViewModel = sharedViewModel,
                    navController = navHostController
                )
            }

            composable(DELIVERY_SCREEN_ROUTE) {
                DeliveryScreen()
            }

            composable(ORDER_SCREEN_ROUTE) {
                OrderScreen(
                    cartViewModel = cartViewModel,
                    navController = navHostController
                )
            }



            composable(FAVORITES_SCREEN_ROUTE) {
                FavoritesScreen(
                    cartViewModel = cartViewModel,
                    sharedViewModel = sharedViewModel
                )
            }

            composable(MENU_SCREEN_ROUTE) {
                MenuScreen(
                    navController = navHostController,
                    cartViewModel = cartViewModel,
                    sharedViewModel = sharedViewModel
                )
            }

            composable(SPLASH_SCREEN_ROUTE) {
                SplashScreen()
            }

            composable(
                route = SEARCH_SCREEN_ROUTE_WITH_ARGS,
                arguments = listOf(
                    boolNavArg(KEY_FOCUS_INPUT)
                )
            ) { backStackEntry ->
                val focusInput = backStackEntry.arguments?.getBoolean(KEY_FOCUS_INPUT) == true
                SearchScreen(
                    focusSearchBarInput = focusInput,
                    navController = navHostController,
                    cartViewModel = cartViewModel,
                    sharedViewModel = sharedViewModel
                )
            }

            bottomSheet(
                route = MEAL_DETAILS_ROUTE_WITH_ARGS,
                arguments = listOf(
                    jsonNavArg(KEY_MEAL_JSON),
                    boolNavArg(KEY_IS_EDIT_MODE)
                )
            ) { backStackEntry ->
                val isEditMode = backStackEntry.arguments?.getBoolean(KEY_IS_EDIT_MODE) == true
                val meal = backStackEntry.decodeJsonArg<CustomizedMeal>(KEY_MEAL_JSON, gson)

                MealDetailsBottomSheet(
                    sharedViewModel = sharedViewModel,
                    cartViewModel = cartViewModel,
                    initItem = meal,
                    isEditMode = isEditMode,
                    onClose = { navHostController.popBackStack() }
                )
            }

            composable(
                route = ADDRESS_SCREEN_ROUTE_WITH_ARGS,
                arguments = listOf(
                    jsonNavArg(KEY_ADDRESS_JSON)
                )
            ) { backStackEntry ->
                val address = backStackEntry.decodeJsonArg<Address>(KEY_ADDRESS_JSON, gson)
                AddressMapScreen(navController = navHostController, initAddress = address)
            }

            composable(
                route = ADDRESS_DETAILS_ROUTE_WITH_ARGS,
                arguments = listOf(
                    jsonNavArg(KEY_ADDRESS_JSON),
                    boolNavArg(KEY_IS_EDIT_MODE)
                )
            ) { backStackEntry ->
                val isEditMode = backStackEntry.arguments?.getBoolean(KEY_IS_EDIT_MODE) == true
                val address = backStackEntry.decodeJsonArg<Address>(KEY_ADDRESS_JSON, gson)
                AddressDetailsScreen(
                    isEditMode = isEditMode,
                    initAddress = address,
                    navController = navHostController
                )
            }
        }
    }
}

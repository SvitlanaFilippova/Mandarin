package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.cart.presentation.screen.CartScreen
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.delivery.presentation.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.location.presentation.ui.screen.LocationScreen
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.presentation.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.order.presentation.ui.screen.OrderScreen
import com.mandarinkafe.mandarin.features.search.presentation.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.navigation.NavConstants.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_FOCUS_INPUT
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_IS_EDIT_MODE
import com.mandarinkafe.mandarin.navigation.NavConstants.KEY_MEAL_JSON
import com.mandarinkafe.mandarin.navigation.NavConstants.LOCATION_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.MEAL_DETAILS_ROUTE_WITH_ARGS
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.ORDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.SEARCH_SCREEN_ROUTE_WITH_ARGS
import com.mandarinkafe.mandarin.navigation.NavConstants.SPLASH_SCREEN_ROUTE
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

            composable(LOCATION_SCREEN_ROUTE) {
                LocationScreen(navController = navHostController)
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
                    navArgument(KEY_FOCUS_INPUT) {
                        type = NavType.BoolType
                        defaultValue = false // по умолчанию не фокусируем
                    }
                )
            ) { entry ->
                val focusInput = entry.arguments?.getBoolean(KEY_FOCUS_INPUT) == true

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
                    navArgument(KEY_MEAL_JSON) { type = NavType.StringType },
                    navArgument(KEY_IS_EDIT_MODE) { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString(KEY_MEAL_JSON)?.let {
                    URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                }

                val isEditMode = backStackEntry.arguments?.getBoolean(KEY_IS_EDIT_MODE) == true

                val meal = remember(json) {
                    gson.fromJson(json, CustomizedMeal::class.java)
                }

                MealDetailsBottomSheet(
                    sharedViewModel = sharedViewModel,
                    cartViewModel = cartViewModel,
                    initItem = meal,
                    isEditMode = isEditMode,
                    onClose = { navHostController.popBackStack() }
                )
            }
        }
    }
}

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
import com.mandarinkafe.mandarin.features.delivery.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.favorites.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.search.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.navigation.NavRoutes.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.shared.cart.ui.screen.CartScreen
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.splash.ui.SplashScreen
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
                    sharedViewModel = sharedViewModel
                )
            }

            composable(DELIVERY_SCREEN_ROUTE) {
                DeliveryScreen()
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
                SplashScreen(
                    onFinished = {
                        navHostController.navigate(MENU_SCREEN_ROUTE) {
                            popUpTo(SPLASH_SCREEN_ROUTE) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = "$SEARCH_SCREEN_ROUTE?focusInput={focusInput}",
                arguments = listOf(
                    navArgument("focusInput") {
                        type = NavType.BoolType
                        defaultValue = false // по умолчанию не фокусируем
                    }
                )
            ) { entry ->
                val focusInput = entry.arguments?.getBoolean("focusInput") == true

                SearchScreen(
                    focusSearchBarInput = focusInput,
                    navController = navHostController,
                    cartViewModel = cartViewModel,
                    sharedViewModel = sharedViewModel
                )
            }

            bottomSheet(
                route = "meal_details/{mealJson}/{isEditMode}",
                arguments = listOf(
                    navArgument("mealJson") { type = NavType.StringType },
                    navArgument("isEditMode") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("mealJson")?.let {
                    URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                }

                val isEditMode = backStackEntry.arguments?.getBoolean("isEditMode") == true

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

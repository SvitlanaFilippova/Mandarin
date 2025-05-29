package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.mandarinkafe.mandarin.features.cart.ui.screen.CartScreen
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.features.delivery.screen.DeliveryScreen
import com.mandarinkafe.mandarin.features.favorites.ui.screen.FavoritesScreen
import com.mandarinkafe.mandarin.features.menu.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.features.search.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.navigation.NavRoutes.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.splash.ui.SplashScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun NavGraph(navHostController: NavHostController) {
    val cartViewModel: CartViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    NavHost(
        navController = navHostController,
        startDestination = SPLASH_SCREEN_ROUTE
    ) {
        composable(CART_SCREEN_ROUTE) {
            CartScreen(viewModel = cartViewModel)
        }

        composable(DELIVERY_SCREEN_ROUTE) {
            DeliveryScreen()
        }

        composable(FAVORITES_SCREEN_ROUTE) {
            FavoritesScreen(
                cartViewModel = cartViewModel
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
                navController = navHostController,
                cartViewModel = cartViewModel,
                focusSearchBarInput = focusInput
            )
        }
    }
}

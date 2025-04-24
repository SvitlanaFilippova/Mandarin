package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mandarinkafe.mandarin.cart.ui.CartScreen
import com.mandarinkafe.mandarin.cart.ui.CartViewModel
import com.mandarinkafe.mandarin.delivery.screen.DeliveryScreen
import com.mandarinkafe.mandarin.favorites.screen.FavoritesScreen
import com.mandarinkafe.mandarin.menu.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.navigation.NavRoutes.APP_SCOPE_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCOPE_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ARG_FOCUS
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.search.ui.screen.SearchScreen

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = APP_SCOPE_ROUTE
    ) {
        // Глобальный AppScope
        navigation(
            route = APP_SCOPE_ROUTE,
            startDestination = MENU_SCOPE_ROUTE
        ) {

            // CartViewModel живёт на уровне AppScope
            composable(CART_SCREEN_ROUTE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(APP_SCOPE_ROUTE)
                }
                val cartViewModel: CartViewModel = hiltViewModel(parentEntry)
                CartScreen(viewModel = cartViewModel)
            }

            composable(DELIVERY_SCREEN_ROUTE) {
                DeliveryScreen()
            }

            composable(FAVORITES_SCREEN_ROUTE) { entry ->
                val appScopeEntry = remember(entry) {
                    navHostController.getBackStackEntry(APP_SCOPE_ROUTE)
                }
                val cartViewModel: CartViewModel = hiltViewModel(appScopeEntry)
                val menuViewModel: MenuViewModel = hiltViewModel()

                FavoritesScreen(
                    menuViewModel = menuViewModel,
                    cartViewModel = cartViewModel
                )
            }

            // Меню + Поиск в MenuScope
            navigation(
                route = MENU_SCOPE_ROUTE,
                startDestination = NavRoutes.MENU_SCREEN_ROUTE
            ) {
                composable(NavRoutes.MENU_SCREEN_ROUTE) { entry ->
                    val parentEntry = remember(entry) {
                        navHostController.getBackStackEntry(MENU_SCOPE_ROUTE)
                    }
                    val menuViewModel: MenuViewModel = hiltViewModel(parentEntry)

                    val appScopeEntry = remember(entry) {
                        navHostController.getBackStackEntry(APP_SCOPE_ROUTE)
                    }
                    val cartViewModel: CartViewModel = hiltViewModel(appScopeEntry)

                    MenuScreen(
                        menuViewModel = menuViewModel,
                        navController = navHostController,
                        cartViewModel = cartViewModel
                    )
                }

                composable(
                    route = "${SEARCH_SCREEN_ROUTE}/{${SEARCH_SCREEN_ARG_FOCUS}}",
                    arguments = listOf(navArgument(SEARCH_SCREEN_ARG_FOCUS) {
                        type = NavType.BoolType
                    })
                ) { entry ->
                    val parentEntry = remember(entry) {
                        navHostController.getBackStackEntry(MENU_SCOPE_ROUTE)
                    }
                    val menuViewModel: MenuViewModel = hiltViewModel(parentEntry)

                    val appScopeEntry = remember(entry) {
                        navHostController.getBackStackEntry(APP_SCOPE_ROUTE)
                    }
                    val cartViewModel: CartViewModel = hiltViewModel(appScopeEntry)

                    val focusInput = entry.arguments?.getBoolean(SEARCH_SCREEN_ARG_FOCUS) == true
                    SearchScreen(
                        navController = navHostController,
                        focusSearchBarInput = focusInput,
                        menuViewModel = menuViewModel,
                        cartViewModel = cartViewModel
                    )
                }
            }
        }
    }
}
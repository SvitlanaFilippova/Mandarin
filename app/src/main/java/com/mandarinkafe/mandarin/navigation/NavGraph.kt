package com.mandarinkafe.mandarin.navigation

import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mandarinkafe.mandarin.delivery.screen.DeliveryScreen
import com.mandarinkafe.mandarin.favorites.FavoritesFragment
import com.mandarinkafe.mandarin.menu.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.navigation.NavRoutes.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCOPE_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.PLACEHOLDER_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ARG_FOCUS
import com.mandarinkafe.mandarin.navigation.NavRoutes.SEARCH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.placeholder.ui.screen.PlaceholderScreen
import com.mandarinkafe.mandarin.search.ui.screen.SearchScreen

@Composable
fun NavGraph(navHostController: NavHostController, fragmentManager: FragmentManager) {
    NavHost(
        navController = navHostController,
        startDestination = MENU_SCOPE_ROUTE
    ) {

        navigation(route = MENU_SCOPE_ROUTE, startDestination = MENU_SCREEN_ROUTE) {
            //вложенный граф навигации для переиспользования menuViewModel на экране поиска
            composable(MENU_SCREEN_ROUTE) { entry ->
                val parentEntry = remember(entry) {
                    navHostController.getBackStackEntry(MENU_SCOPE_ROUTE)
                }
                val menuViewModel: MenuViewModel = hiltViewModel(parentEntry)
                MenuScreen(
                    viewModel = menuViewModel,
                    navController = navHostController
                )
            }
            composable(
                route = "$SEARCH_SCREEN_ROUTE/{$SEARCH_SCREEN_ARG_FOCUS}",
                arguments = listOf(
                    navArgument(SEARCH_SCREEN_ARG_FOCUS) { type = NavType.BoolType }
                )) { entry ->
                val parentEntry = remember(entry) {
                    navHostController.getBackStackEntry(MENU_SCOPE_ROUTE)
                }
                val menuViewModel: MenuViewModel = hiltViewModel(parentEntry)
                val focusInput = entry.arguments?.getBoolean(SEARCH_SCREEN_ARG_FOCUS) != false
                SearchScreen(
                    viewModel = menuViewModel,
                    navController = navHostController,
                    focusSearchBarInput = focusInput
                )
            }

        }
        composable(DELIVERY_SCREEN_ROUTE) {
            DeliveryScreen()
        }
        composable(PLACEHOLDER_SCREEN_ROUTE) {
            PlaceholderScreen(
                errorMessage = TODO()
            )
        }
        composable(FAVORITES_SCREEN_ROUTE) {
            // Вставьте сюда компоуз экран избранных
            FragmentContainer(fragmentManager, FavoritesFragment())
        }
    }
}

@Composable
fun FragmentContainer(fragmentManager: FragmentManager, fragment: Fragment) {
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply { id = View.generateViewId() }
        },
        update = { frameLayout ->
            fragmentManager.beginTransaction()
                .replace(frameLayout.id, fragment)
                .commitNow()
        }
    )
}
package com.mandarinkafe.mandarin.navigation

import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mandarinkafe.mandarin.delivery.DeliveryFragment
import com.mandarinkafe.mandarin.favorites.FavoritesFragment
import com.mandarinkafe.mandarin.menu.ui.screen.MenuScreen
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.search.ui.screen.SearchScreen
import com.mandarinkafe.mandarin.search.ui.view_model.SearchViewModel
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.util.Constants.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.util.Constants.MENU_SCOPE_ROUTE
import com.mandarinkafe.mandarin.util.Constants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.util.Constants.SEARCH_SCREEN_ROUTE

@Composable
fun NavGraph(navHostController: NavHostController, fragmentManager: FragmentManager) {
    NavHost(
        navController = navHostController,
        startDestination = MENU_SCOPE_ROUTE
    ) {

        navigation(route = MENU_SCOPE_ROUTE, startDestination = MENU_SCREEN_ROUTE) {
            //вложенный граф навигации для переиспользования menuViewModel на экране поиска
            composable(MENU_SCREEN_ROUTE) {
                val menuViewModel: MenuViewModel = hiltViewModel()
                MenuScreen(
                    viewModel = menuViewModel,
                    onSearchClick = { navHostController.navigate(SEARCH_SCREEN_ROUTE) })
            }
            composable(SEARCH_SCREEN_ROUTE) {
                val searchViewModel: SearchViewModel = hiltViewModel()
                val menuViewModel: MenuViewModel = hiltViewModel()
                SearchScreen(viewModel = searchViewModel, onMenuEvent = menuViewModel::onEvent)
            }

        }
        composable(DELIVERY_SCREEN_ROUTE) {
            // Вставьте сюда компоуз экран доставки
            FragmentContainer(fragmentManager, DeliveryFragment())
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
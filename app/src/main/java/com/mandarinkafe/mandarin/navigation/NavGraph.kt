package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mandarinkafe.mandarin.delivery.DeliveryFragment
import com.mandarinkafe.mandarin.favorites.FavoritesFragment
import com.mandarinkafe.mandarin.menu.ui.MenuFragment

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = "search"
    ) {
        composable("search") {
            MenuFragment()
        }
        composable("delivery") {
            DeliveryFragment()
        }
        composable("favorites") {
            FavoritesFragment()
        }
    }
}
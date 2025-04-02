package com.mandarinkafe.mandarin.navigation

import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mandarinkafe.mandarin.delivery.screen.DeliveryScreen
import com.mandarinkafe.mandarin.favorites.FavoritesFragment
import com.mandarinkafe.mandarin.menu.ui.screen.MenuScreenPreview

@Composable
fun NavGraph(navHostController: NavHostController, fragmentManager: FragmentManager) {
    NavHost(
        navController = navHostController,
        startDestination = "search"
    ) {
        composable("search") {
           MenuScreenPreview()
        }
        composable("delivery") {
            DeliveryScreen()
        }
        composable("favorites") {
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
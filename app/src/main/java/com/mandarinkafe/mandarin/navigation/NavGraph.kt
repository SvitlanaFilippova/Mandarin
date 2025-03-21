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
import com.mandarinkafe.mandarin.delivery.DeliveryFragment
import com.mandarinkafe.mandarin.favorites.FavoritesFragment
import com.mandarinkafe.mandarin.menu.ui.MenuFragment

@Composable
fun NavGraph(navHostController: NavHostController, fragmentManager: FragmentManager) {
    NavHost(
        navController = navHostController,
        startDestination = "search"
    ) {
        composable("search") {
            FragmentContainer(fragmentManager, MenuFragment())
        }
        composable("delivery") {
            FragmentContainer(fragmentManager, DeliveryFragment())
        }
        composable("favorites") {
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
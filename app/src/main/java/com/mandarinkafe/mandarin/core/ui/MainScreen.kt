package com.mandarinkafe.mandarin.core.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.navigation.BottomNavigation
import com.mandarinkafe.mandarin.navigation.NavGraph
import com.mandarinkafe.mandarin.navigation.NavRoutes.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.shared.cart.ui.components.FavoriteVariantChoiceDialog
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedViewModel
import com.mandarinkafe.mandarin.util.ui.components.AppTopBar
import com.mandarinkafe.mandarin.util.ui.components.HandleEffects

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val cartViewModel: CartViewModel = hiltViewModel()
    val cartState by cartViewModel.state.collectAsState()
    val cartCount = cartState.cartItemsCount
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val sharedState by sharedViewModel.state.collectAsState()
    val effectFlow = sharedViewModel.effect
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isSplash = currentRoute == SPLASH_SCREEN_ROUTE
    val showBottomBar = !isSplash
    val showTopBar = !isSplash && sharedState.shouldShowTopBar
    val onEvent = sharedViewModel::onEvent
    val selectedMeal = sharedState.selectedMealForFavoriteChoice


    Scaffold(
        topBar = {
            AppTopBar(
                visible = showTopBar,
                onEvent = onEvent
            )
        },
        bottomBar = {
            BottomNavigation(
                visible = showBottomBar,
                navController = navController,
                cartCount = cartCount,
            )

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Colors.AppBlack)
        ) {
            NavGraph(
                navHostController = navController
            )
        }
    }

    if (sharedState.showFavoriteDialog && selectedMeal != null) {
        FavoriteVariantChoiceDialog(
            onBaseSelected = {
                onEvent(SharedEvent.ToggleFavorite(meal = selectedMeal.meal))
                onEvent(SharedEvent.DismissFavoriteDialog)
            },
            onCustomSelected = {
                onEvent(SharedEvent.ToggleFavorite(item = selectedMeal))
                onEvent(SharedEvent.DismissFavoriteDialog)
            },
            onDismiss = {
                onEvent(SharedEvent.DismissFavoriteDialog)
            }
        )
    }


    LaunchedEffect(currentRoute) {
        // Сбрасываем состояние топбара при изменении маршрута
        if (currentRoute != null && currentRoute != SPLASH_SCREEN_ROUTE) {
            sharedViewModel.onEvent(SharedEvent.ResetTopBar)
        }
    }
    HandleEffects(
        effectFlow = effectFlow,
        navController = navController,
    )
}

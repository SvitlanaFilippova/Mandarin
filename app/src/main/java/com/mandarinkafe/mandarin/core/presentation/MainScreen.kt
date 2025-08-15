package com.mandarinkafe.mandarin.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.components.FavoriteVariantChoiceDialog
import com.mandarinkafe.mandarin.navigation.NavConstants.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavConstants.bottomNavigationRoutes
import com.mandarinkafe.mandarin.navigation.NavGraph
import com.mandarinkafe.mandarin.navigation.bottomnav.BottomNavigation
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.AppTopBar
import com.mandarinkafe.mandarin.util.presentation.ui.components.CustomSnackbarHost
import com.mandarinkafe.mandarin.util.presentation.ui.components.HandleEffects

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val sharedState by sharedViewModel.state.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var isSplashLoading by remember { mutableStateOf(true) }
    val isSplash = if (isSplashLoading) true else currentRoute == SPLASH_SCREEN_ROUTE
    LaunchedEffect(currentRoute) {
        if (isSplashLoading && currentRoute != null && currentRoute != SPLASH_SCREEN_ROUTE) {
            isSplashLoading = false
        }
    }

    val showTopBar = !isSplash && sharedState.shouldShowTopBar
    val onEvent = sharedViewModel::onEvent
    val selectedMeal = sharedState.selectedMealForFavoriteChoice
    val snackbarHostState = remember { SnackbarHostState() }
    val isInnerScreen = currentRoute?.let { route -> route !in bottomNavigationRoutes } == true

    val showBottomBar = !isSplash && !isInnerScreen

    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                showAppBar = showTopBar,
                onEvent = onEvent,
            )
        },
        bottomBar = {
            BottomNavigation(
                visible = showBottomBar,
                navController = navController,
                cartCount = sharedState.cartItemsCount,
            )

        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
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
        effectFlow = sharedViewModel.effect,
        snackbarHostState = snackbarHostState,
        navController = navController,
    )
}

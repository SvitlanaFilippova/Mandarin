package com.mandarinkafe.mandarin.menu.ui.screen

import android.content.Intent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuViewModel
import com.mandarinkafe.mandarin.navigation.navigateToFavoritesScreen
import com.mandarinkafe.mandarin.navigation.navigateToSearchScreen
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.ui.components.PlaceholderScreen

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    navController: NavHostController
) {

    val menuSate by menuViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val effectFlow = menuViewModel.effect
    val listState = rememberLazyListState()
    val context = LocalContext.current

    when {
        menuSate.isLoading -> LoadingScreen()
        menuSate.errorMessage != null -> PlaceholderScreen(
            menuSate.errorMessage!!,
            onEvent = menuViewModel::onEvent,
        )

        else -> MenuContentScreen(
            listState = listState,
            onEvent = menuViewModel::onEvent,
            onCartEvent = cartViewModel::onEvent,
            menuSate = menuSate,
            cartState = cartState
        )
    }
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MenuContract.Effect.ShowSnackbar -> {
                    // показать снекбар
                }

                is MenuContract.Effect.OpenSearch -> {
                    navController.navigateToSearchScreen(focusInput = effect.focusSearch)
                }

                is MenuContract.Effect.OpenFavorites -> {
                    navController.navigateToFavoritesScreen()
                }

                MenuContract.Effect.CallPhone -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = PHONE_NUMBER.toUri()
                    }
                    context.startActivity(intent)
                }

                is MenuContract.Effect.OpenMealDetailsBS -> {
                    // Обрабатывается отдельно в HandleBottomSheetEffect
                }

            }
        }
    }


    HandleBottomSheetEffect<MenuContract.Effect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? MenuContract.Effect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initMeal = effect.meal,
            onDismiss = onDismiss,
            onFavoriteChanged = { id, isFavorite ->
                menuViewModel.onEvent(MenuContract.Event.UpdateMealFavorite(id, isFavorite))
            },
            shouldOpenCustomizationInit = effect.shouldOpenCustomization,
            onCartEvent = cartViewModel::onEvent
        )
    }
}

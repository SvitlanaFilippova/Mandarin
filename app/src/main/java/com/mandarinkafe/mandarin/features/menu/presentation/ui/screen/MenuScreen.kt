package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToSearchScreen
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    val cartItems by cartViewModel.state.map { it.cartItems }
        .collectAsStateWithLifecycle(emptyList())
    val cartInProgressItems by cartViewModel.state.map { it.inProgressItems }
        .collectAsStateWithLifecycle(emptySet())
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    val favoriteIds by sharedViewModel.favoritesIDs.collectAsStateWithLifecycle(emptySet())

    val menuItems = state.menuItems
    val banners = state.banners
    val bannersAreLoading = state.bannersAreLoading
    val selectedTabIndex = state.selectedTabIndex
    val selectedSubTabIndex = state.selectedSubTabIndex
    val selectedMenuItemIndex = state.selectedMenuItemIndex
    val error = state.error
    val onSharedEvent = sharedViewModel::onEvent
    val onMenuEvent = menuViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent

    val effectFlow = menuViewModel.effect
    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onMenuEvent(MenuContract.MenuEvent.ForceRefresh) }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when {
            error != null -> PlaceholderScreen(
                error = error,
                onRetryClick = { onMenuEvent(MenuContract.MenuEvent.ForceRefresh) },
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            menuItems.isEmpty() -> PlaceholderScreen(
                error = UiError.MenuEmpty,
                onRetryClick = { onMenuEvent(MenuContract.MenuEvent.ForceRefresh) },
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            else -> MenuContentScreen(
                listState = listState,
                onMenuEvent = onMenuEvent,
                onSharedEvent = onSharedEvent,
                cartItems = cartItems,
                inProgressItems = cartInProgressItems,
                menuItems = menuItems,
                banners = banners,
                bannersAreLoading = bannersAreLoading,
                selectedTabIndex = selectedTabIndex,
                selectedSubTabIndex = selectedSubTabIndex,
                selectedMenuItemIndex = selectedMenuItemIndex,
                favoriteIds = favoriteIds,
                onMealDetailsClick = { meal -> onSharedEvent(SharedEvent.OnMealDetailsClick(meal)) },
                onToggleFavorite = { meal -> onSharedEvent(SharedEvent.ToggleFavorite(meal)) },
                onAddToCart = { meal -> onCartEvent(CartEvent.AddToCart(customizedMeal = meal.toCustomizedMeal())) },
                onRemoveFromCart = { meal -> onCartEvent(CartEvent.OnReduce(meal = meal)) },
            )

        }

        LaunchedEffect(effectFlow) {
            effectFlow.collect { effect ->
                if (effect is MenuContract.MenuEffect.OpenSearch
                ) {
                    navController.navigateToSearchScreen(effect.focusSearch)
                }
            }
        }
        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

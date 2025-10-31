package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberMenuViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.flow.map

@Composable
fun MenuScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
) {
    val menuViewModel = rememberMenuViewModel()
    val state by menuViewModel.state.collectAsState()

    val cartItemsFlow = remember { cartViewModel.state.map { it.cartItems } }
    val cartItems by cartItemsFlow.collectAsState(initial = emptyList())

    val cartInProgressItemsFlow = remember { cartViewModel.state.map { it.inProgressItems } }
    val cartInProgressItems by cartInProgressItemsFlow.collectAsState(initial = emptySet())

    val favoriteIds by sharedViewModel.favoritesIDs.collectAsState(emptySet())

    val menuItems = state.menuItems
    val banners = state.banners
    val bannersAreLoading = state.bannersAreLoading
    val error = state.error
    val onSharedEvent = sharedViewModel::onEvent
    val onMenuEvent = menuViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent

    val sharedEffectFlow = sharedViewModel.effect

    val isLoading = state.isLoading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { onMenuEvent(MenuEvent.ForceRefresh) }
    )

    PullRefreshLayout(
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        indicator = {
            PullRefreshIndicator(
                state = pullRefreshState,
                contentColor = Colors.Orange,
            )
        }
    ) {
        when {
            error != null -> PlaceholderScreen(
                error = error,
                onRetryClick = { onMenuEvent(MenuEvent.ForceRefresh) },
                onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
            )

            menuItems.isEmpty() && !isLoading ->
                PlaceholderScreen(
                    error = UiError.MenuEmpty,
                    onRetryClick = { onMenuEvent(MenuEvent.ForceRefresh) },
                    onCallClick = { onSharedEvent(SharedEvent.OnPhoneClick) },
                )

            menuItems.isNotEmpty() -> {
                MenuContentScreen(
                    menuItems = menuItems,
                    favoriteIds = favoriteIds,
                    inProgressItems = cartInProgressItems,
                    cartItems = cartItems,
                    onAddToCart = { meal ->
                        onCartEvent(CartEvent.AddToCart(customizedMeal = meal.toCustomizedMeal()))
                    },
                    onRemoveFromCart = { meal ->
                        onCartEvent(CartEvent.OnReduce(meal = meal))
                    },
                    onToggleFavorite = { meal ->
                        onSharedEvent(SharedEvent.ToggleFavorite(meal))
                    },
                    onMealDetailsClick = { meal ->
                        onSharedEvent(SharedEvent.OnMealDetailsClick(meal))
                    },
                    onBannersClick = {
                        onMenuEvent(MenuEvent.BannerClick(it))
                    },
                    bannersAreLoading = bannersAreLoading,
                    selectedMenuItemIndex = state.selectedMenuItemIndex,
                    banners = banners,
                    sharedEffectFlow = sharedEffectFlow
                )
            }
        }
    }
}

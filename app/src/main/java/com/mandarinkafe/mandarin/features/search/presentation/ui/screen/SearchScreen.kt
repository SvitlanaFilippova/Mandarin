package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract.SearchEffect
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    focusSearchBarInput: Boolean = false
) {
    val searchState by searchViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val favoriteIds by sharedViewModel.favoritesIDs.collectAsState()
    val effectFlow = searchViewModel.effect
    val onEvent = searchViewModel::onEvent

    SearchScreenContent(
        focusSearchBarInput = focusSearchBarInput,
        cartItems = cartState.cartItems,
        favoriteIds = favoriteIds,
        onSearchEvent = onEvent,
        searchState = searchState,
        onMealDetailsClick = { meal -> onSharedEvent(SharedEvent.OnMealDetailsClick(meal)) },
        onToggleFavorite = { meal -> onSharedEvent(SharedEvent.ToggleFavorite(meal)) },
        onAddToCart = { meal -> onCartEvent(CartEvent.AddToCart(customizedMeal = meal.toCustomizedMeal())) },
        onRemoveFromCart = { meal -> onCartEvent(CartEvent.OnReduce(meal = meal)) },
        onSearchDismiss = { onEvent(SearchEvent.GoBackToMenu) },
    )

    // Отлавливаем эффект возврата в меню
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            if (effect is SearchEffect.GoBackToMenuEffect && !navController.popBackStack()) {
                navController.navigateToMenu()
            }
        }
    }
}
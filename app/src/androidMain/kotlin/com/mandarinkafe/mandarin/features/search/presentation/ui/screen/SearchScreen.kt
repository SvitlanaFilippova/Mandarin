package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinViewModel(),
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    focusSearchBarInput: Boolean = false,
    onBackClick: () -> Unit
) {
    val searchState by searchViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val onSharedEvent = sharedViewModel::onEvent
    val onCartEvent = cartViewModel::onEvent
    val favoriteIds by sharedViewModel.favoritesIDs.collectAsState()
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
        inProgressItems = cartState.inProgressItems,
        onBackClick = onBackClick,
    )

}
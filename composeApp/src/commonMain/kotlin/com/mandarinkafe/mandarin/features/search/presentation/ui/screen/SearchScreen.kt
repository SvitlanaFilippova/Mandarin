package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberSearchViewModel

@Composable
fun SearchScreen(
    focusSearchBarInput: Boolean,
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
) {
    val searchViewModel = rememberSearchViewModel()
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
        onMealDetailsClick = { meal -> onSharedEvent(SharedContract.SharedEvent.OnMealDetailsClick(meal)) },
        onToggleFavorite = { meal -> onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(meal)) },
        onAddToCart = { meal -> onCartEvent(CartEvent.AddToCart(customizedMeal = meal.toCustomizedMeal())) },
        onRemoveFromCart = { meal -> onCartEvent(CartEvent.OnReduce(meal = meal)) },
        inProgressItems = cartState.inProgressItems,
    )

}

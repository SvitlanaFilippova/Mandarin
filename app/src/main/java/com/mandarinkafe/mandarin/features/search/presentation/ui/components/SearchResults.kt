package com.mandarinkafe.mandarin.features.search.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen

@Composable
fun SearchResults(
    filteredMenuItems: List<Meal>,
    latestSearchText: String,
    favoriteIds: Set<String>,
    cartItems: List<CartItem>,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    inProgressItems: Set<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Transparent)
    ) {
        if (filteredMenuItems.isNotEmpty()) {
            SearchResultsLazyColumn(
                filteredMenuItems = filteredMenuItems,
                cartItems = cartItems,
                favoriteIds = favoriteIds,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onMealDetailsClick = onMealDetailsClick,
                inProgressItems = inProgressItems,
            )
        } else if (latestSearchText.isNotEmpty()) {
            PlaceholderScreen(error = UiError.SearchEmpty)
        }
    }

}
package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract

@Composable
fun SearchResultsLazyColumn(
    filteredMenuItems: List<Meal>,
    cartState: CartContract.CartState,
    favoriteIds: Set<String>,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
) {
    LazyColumn {
        itemsIndexed(filteredMenuItems) { index, meal ->
            SmallHorizontalMealItemCard(
                meal = meal,
                cartState = cartState,
                favoriteIds = favoriteIds,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onMealDetailsClick = onMealDetailsClick,
            )
        }
    }
}

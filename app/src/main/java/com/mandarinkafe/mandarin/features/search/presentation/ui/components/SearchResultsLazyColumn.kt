package com.mandarinkafe.mandarin.features.search.presentation.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Constants

@Composable
fun SearchResultsLazyColumn(
    filteredMenuItems: List<Meal>,
    cartItems: List<CartItem>,
    favoriteIds: Set<String>,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    inProgressItems: Set<String>
) {
    LazyColumn {
        items(
            items = filteredMenuItems,
            key = { it.id }
        ) { meal ->
            SmallHorizontalMealItemCard(
                modifier = Modifier.animateItem(tween(Constants.ANIMATION_DURATION_FAST)),
                meal = meal,
                cartItems = cartItems,
                favoriteIds = favoriteIds,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onMealDetailsClick = onMealDetailsClick,
                isInProgress = meal.id in inProgressItems,
            )
        }
    }
}

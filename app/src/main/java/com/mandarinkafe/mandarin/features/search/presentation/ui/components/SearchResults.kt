package com.mandarinkafe.mandarin.features.search.presentation.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen

@Composable
fun SearchResults(
    filteredMenuItems: List<Meal>,
    latestSearchText: String,
    favoriteIds: Set<String>,
    onSearchDismiss: () -> Unit,
    cartState: CartContract.CartState,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
) {
    BackHandler {
        onSearchDismiss()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = Dimens.RadiusSearchField8,
                    bottomEnd = Dimens.RadiusSearchField8
                )
            )
            .padding(top = Dimens.ZeroDp0)
            .background(Colors.Transparent)

    ) {
        if (filteredMenuItems.isNotEmpty()) {
            SearchResultsLazyColumn(
                filteredMenuItems = filteredMenuItems,
                cartState = cartState,
                favoriteIds = favoriteIds,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onMealDetailsClick = onMealDetailsClick,
            )
        } else if (latestSearchText.isNotEmpty()) {
            PlaceholderScreen(error = UiError.SearchEmpty)
        }
    }

}
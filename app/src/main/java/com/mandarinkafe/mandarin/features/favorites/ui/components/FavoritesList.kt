package com.mandarinkafe.mandarin.features.favorites.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartEvent
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartState
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEffect
import com.mandarinkafe.mandarin.features.favorites.ui.view_model.FavoritesContract.FavoritesEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun FavoritesContent(
    data: List<CustomizedMeal>,
    onEvent: (FavoritesEvent) -> Unit,
    onCartEvent: (CartEvent) -> Unit,
    cartState: CartState,
    effectFlow: Flow<FavoritesEffect>,
) {

    LazyColumn {
        itemsIndexed(data) { index, item ->
            FavoritesItemCard(
                item = item,
                onEvent = onEvent,
                onCartEvent = onCartEvent,
                cartState = cartState
            )
        }
    }
}
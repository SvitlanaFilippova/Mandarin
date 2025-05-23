package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract

@Composable
fun SearchResultsLazyColumn(
    filteredMenuItems: List<Meal>,
    onSearchEvent: (SearchContract.SearchEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
) {
    LazyColumn {
        itemsIndexed(filteredMenuItems) { index, meal ->
            SmallHorizontalMealItemCard(
                meal = meal,
                onCartEvent = onCartEvent,
                cartState = cartState,
                onSearchEvent = onSearchEvent,
            )
        }

    }
}

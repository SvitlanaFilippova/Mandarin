package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract

@Composable
fun SearchResultsLazyColumn(
    filteredMenuItems: List<MenuItem>,
    onSearchEvent: (SearchContract.Event) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
    ) {
        itemsIndexed(filteredMenuItems) { index, item ->
            if (item is MenuItem.MealItem) {
                SearchResultsMealItem(
                    meal = item.meal,
                    onCartEvent = onCartEvent,
                    cartState = cartState,
                    onSearchEvent = onSearchEvent,
                )
            }
        }
    }
}

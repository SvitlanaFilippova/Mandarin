package com.mandarinkafe.mandarin.search.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun SearchResultsLazyColumn(
    filteredMenuItems: List<MenuItem>,
    onMealClick: (Meal) -> Unit,
    onEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    cartState: CartContract.State,
) {
    LazyColumn(
        modifier = Modifier.padding(Dimens.MarginStandard16),
    ) {
        itemsIndexed(filteredMenuItems) { index, item ->
            if (item is MenuItem.MealItem) {
                SearchResultsMealItem(
                    meal = item.meal,
                    onEvent = onEvent,
                    onItemClick = {
                        onMealClick(it)

                    },
                    onCartEvent = onCartEvent,
                    cartState = cartState,
                )
            }
        }
    }
}

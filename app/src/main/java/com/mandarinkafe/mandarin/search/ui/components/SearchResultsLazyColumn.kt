package com.mandarinkafe.mandarin.search.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun SearchResultsLazyColumn(
    filteredMenuItems: List<MenuRVItem>,
    onMealClick: (Meal) -> Unit,
    onEvent: (Event) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(Dimens.MarginStandard16),
    ) {
        itemsIndexed(filteredMenuItems) { index, item ->
            if (item is MenuRVItem.MealItem) {
                SearchResultsMealItem(
                    meal = item.meal,
                    onEvent = onEvent,
                    onItemClick = {
                        onMealClick(it)
                    }
                )
            }
        }
    }
}

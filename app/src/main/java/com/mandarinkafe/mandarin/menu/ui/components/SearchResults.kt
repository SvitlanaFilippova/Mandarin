package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.MenuMealSmallItem

@Composable
fun SearchResults(
    filteredMenuItems: List<MenuRVItem>,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onCustomizeClick: (Meal) -> Unit,
    onMealClick: (Meal) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(Dimens.MarginStandard16),
    ) {
        itemsIndexed(filteredMenuItems) { index, item ->
            if (item is MenuRVItem.MealItem) {
                MenuMealSmallItem(
                    meal = item.meal, onToggleFavorite = onToggleFavorite,
                    onAddToCart = onAddToCart,
                    onRemoveFromCart = onRemoveFromCart,
                    onCustomizeClick = onCustomizeClick,
                    onItemClick = {
                        onMealClick(it)
                    }
                )

            }
        }
    }
}




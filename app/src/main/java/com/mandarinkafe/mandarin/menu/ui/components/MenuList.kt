package com.mandarinkafe.mandarin.menu.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.components.tabs.MenuHeaderItem
import com.mandarinkafe.mandarin.menu.ui.components.tabs.MenuSubHeaderItem
import com.mandarinkafe.mandarin.util.RVItem

@Composable
fun MenuList(
    menuItems: List<RVItem>,
    listState: LazyListState,
    modifier: Modifier,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
) {
    Log.d("DEBUG", "MenuList старт. Меню: ${menuItems.take(10)}")
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(menuItems) { index, item ->
            when (item) {
                is MenuRVItem.HeaderItem -> MenuHeaderItem(item)

                is MenuRVItem.SubHeaderItem -> {
                    val previousItem = if (index > 0) menuItems[index - 1] else null
                    val hasHeaderBefore = previousItem is MenuRVItem.HeaderItem
                    MenuSubHeaderItem(item, hasHeaderBefore)
                }

                is MenuRVItem.MealItem -> MenuMealItem(
                    meal = item.meal, onToggleFavorite = onToggleFavorite,
                    onAddToCart = onAddToCart,
                    onRemoveFromCart = onRemoveFromCart
                )
            }
        }
    }
}
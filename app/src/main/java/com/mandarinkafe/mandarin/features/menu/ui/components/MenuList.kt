package com.mandarinkafe.mandarin.features.menu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.MenuMealItem
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.Event
import kotlinx.coroutines.flow.Flow

@Composable
fun MenuList(
    menuItems: List<MenuItem>,
    listState: LazyListState,
    modifier: Modifier,
    effectFlow: Flow<MenuContract.Effect>,
    onEvent: (Event) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState
) {
    HandleBottomSheetEffects(
        effectFlow = effectFlow,
        onMenuEvent = onEvent,
        onAddToCart = { item ->
            onCartEvent(CartContract.CartEvent.AddToCart(item))
        }
    )

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(menuItems) { index, item ->
            when (item) {
                is MenuItem.HeaderItem -> MenuHeaderItem(item)

                is MenuItem.SubHeaderItem -> {
                    val previousItem = if (index > 0) menuItems[index - 1] else null
                    val hasHeaderBefore = previousItem is MenuItem.HeaderItem
                    MenuSubHeaderItem(item, hasHeaderBefore)
                }

                is MenuItem.MealItem -> MenuMealItem(
                    meal = item.meal,
                    onEvent = onEvent,
                    onCartEvent = onCartEvent,
                    cartState = cartState
                )
            }
        }
    }
}
package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem.MenuCompactMealItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem.MenuMealItem
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT

@Composable
fun MenuList(
    menuItems: List<MenuItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    listState: LazyListState,
    modifier: Modifier,
    cartItems: List<CartItem>,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    ) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = Dimens.MarginSmall8
    val imageSize = remember(screenWidth) {
        (screenWidth - horizontalPadding * MENU_IMAGE_SPACING_COUNT) / MENU_IMAGE_COLUMN_COUNT
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(
            items = menuItems,
            key = { _, item -> item.id }
        ) { index, item ->

            when (item) {
                is MenuItem.HeaderItem -> MenuHeaderItem(item)
                is MenuItem.SubHeaderItem -> {
                    val previousItem = if (index > 0) menuItems[index - 1] else null
                    val hasHeaderBefore = previousItem is MenuItem.HeaderItem
                    MenuSubHeaderItem(item, hasHeaderBefore)
                }

                is MenuItem.MealItem.SingleMealItem -> {
                    val isInProgress = item.meal.id in inProgressItems
                    // Одинарный формат
                    MenuMealItem(
                        meal = item.meal,
                        onToggleFavorite = onToggleFavorite,
                        onAddToCart = onAddToCart,
                        onRemoveFromCart = onRemoveFromCart,
                        cartItems = cartItems,
                        imageSize = imageSize,
                        onMealDetailsClick = onMealDetailsClick,
                        favoriteIds = favoriteIds,
                        isInProgress = isInProgress,
                    )
                }

                is MenuItem.MealItem.MealRow -> {
                    // Пара компактных карточек
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(horizontal = Dimens.MarginSmall8)
                    ) {
                        MenuCompactMealItem(
                            meal = item.left,
                            onToggleFavorite = onToggleFavorite,
                            onAddToCart = onAddToCart,
                            onRemoveFromCart = onRemoveFromCart,
                            cartItems = cartItems,
                            imageSize = imageSize,
                            modifier = Modifier.weight(1f),
                            onMealDetailsClick = onMealDetailsClick,
                            favoriteIds = favoriteIds,
                            isInProgress = item.left.id in inProgressItems,
                        )
                        MenuCompactMealItem(
                            meal = item.right,
                            onToggleFavorite = onToggleFavorite,
                            onAddToCart = onAddToCart,
                            onRemoveFromCart = onRemoveFromCart,
                            cartItems = cartItems,
                            imageSize = imageSize,
                            modifier = Modifier.weight(1f),
                            onMealDetailsClick = onMealDetailsClick,
                            favoriteIds = favoriteIds,
                            isInProgress = item.right.id in inProgressItems,
                        )
                    }

                }
            }
        }
        // Отступ внизу
        item { Spacer(modifier = Modifier.height(Dimens.MarginBig32)) }
    }
}

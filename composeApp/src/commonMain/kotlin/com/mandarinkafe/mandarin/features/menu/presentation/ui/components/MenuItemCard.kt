package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem.MenuCompactMealItem
import com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem.MenuMealItem

@Composable
fun MenuItemCard(
    item: MenuItem,
    previousItem: MenuItem?,
    cartItems: List<CartItem>,
    favoriteIds: Set<String>,
    inProgressItems: Set<String>,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onToggleFavorite: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    imageSize: Dp,
) {
    when (item) {
        is MenuItem.HeaderItem -> MenuHeaderItem(item)
        is MenuItem.SubHeaderItem -> {
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

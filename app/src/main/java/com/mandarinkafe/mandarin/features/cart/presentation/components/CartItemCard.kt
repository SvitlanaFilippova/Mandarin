package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

/**
 * Компонент, который отвечает за отображение товара, который выбрали в меню
 */

@Composable
fun CartItemCard(
    modifier: Modifier,
    item: CartItem,
    itemInPendingDeletion: Boolean,
    favorites: List<CustomizedMeal>,
    deletionProgress: Float,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onShowFavoriteDialog: (CustomizedMeal) -> Unit,
    onAddToCart: (CartItem) -> Unit,
    onRemoveFromCart: (CartItem) -> Unit,
    onMealDetailsClick: (CartItem) -> Unit,
    onDeletionCancel: (CartItem) -> Unit,
    onEditMealClick: (CartItem) -> Unit,
    onCommentAdded: (CartItem, String) -> Unit,
) {
    val contentColor =
        remember(itemInPendingDeletion) { if (itemInPendingDeletion) Colors.LightGreyTransparent75 else Colors.White }

    val onItemClick = if (item.customizedMeal.isCustomized) {
        { onMealDetailsClick(item) }
    } else {
        { onEditMealClick(item) }
    }

    Column(
        modifier = modifier
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable { onItemClick() }

    ) {
        CartItemBaseInfo(
            item = item,
            itemInPendingDeletion = itemInPendingDeletion,
            favorites = favorites,
            contentColor = contentColor,
            onToggleFavorite = onToggleFavorite,
            onShowFavoriteDialog = onShowFavoriteDialog,
            onCommentAdded = { text -> onCommentAdded(item, text) },
        )

        PriceAndButtons(
            item = item,
            itemInPendingDeletion = itemInPendingDeletion,
            deletionProgress = deletionProgress,
            contentColor = contentColor,
            onEditMealClick = { onEditMealClick(item) },
            onMealDetailsClick = { onMealDetailsClick(item) },
            onAddToCart = { onAddToCart(item) },
            onRemoveFromCart = { onRemoveFromCart(item) },
            onDeletionCancel = { onDeletionCancel(item) }
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginSmall8),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.2f)
        )
    }
}

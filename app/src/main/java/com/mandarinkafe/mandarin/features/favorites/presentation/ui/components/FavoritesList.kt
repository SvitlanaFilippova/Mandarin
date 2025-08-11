package com.mandarinkafe.mandarin.features.favorites.presentation.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.id
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitle

@Composable
fun FavoritesContent(
    data: List<CustomizedMeal>,
    cartItems: List<CartItem>,
    inProgressItems: Set<String>,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = Dimens.MarginSmall8

    val imageSize = remember(screenWidth) {
        (screenWidth - horizontalPadding * MENU_IMAGE_SPACING_COUNT) / MENU_IMAGE_COLUMN_COUNT
    }

    ScreenTitle(name = stringResource(R.string.favorite))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        items(
            items = data,
            key = { it.id }
        ) { item ->
            FavoritesItemCard(
                modifier = Modifier.animateItem(tween(Constants.ANIMATION_DURATION_FAST)),
                item = item,
                cartItems = cartItems,
                isInProgress = item.meal.id in inProgressItems,
                imageSize = imageSize,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onMealDetailsClick = onMealDetailsClick,
            )
        }
    }
}

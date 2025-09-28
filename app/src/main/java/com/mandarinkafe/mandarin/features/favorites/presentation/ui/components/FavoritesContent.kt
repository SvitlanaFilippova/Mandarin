package com.mandarinkafe.mandarin.features.favorites.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitle

@SuppressLint("LogNotTimber")
@Composable
fun FavoritesContent(
    data: List<CustomizedMeal>,
    cartItems: List<CartItem>,
    inProgressItems: Set<String>,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
    listState: LazyListState,
) {
    val horizontalPadding = Dimens.MarginSmall8
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenWidthDp = with(density) { windowInfo.containerSize.width.toDp() }

    val imageSize = remember(screenWidthDp) {
        (screenWidthDp - horizontalPadding * MENU_IMAGE_SPACING_COUNT) / MENU_IMAGE_COLUMN_COUNT
    }

    ScreenTitle(name = stringResource(R.string.favorite))
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        items(
            items = data,
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

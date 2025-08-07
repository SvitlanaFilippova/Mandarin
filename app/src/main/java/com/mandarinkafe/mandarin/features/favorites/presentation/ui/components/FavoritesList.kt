package com.mandarinkafe.mandarin.features.favorites.presentation.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_COLUMN_COUNT
import com.mandarinkafe.mandarin.util.Constants.MENU_IMAGE_SPACING_COUNT

@Composable
fun FavoritesContent(
    data: List<CustomizedMeal>,
    cartItems: List<CartItem>,
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

    Text(
        modifier = Modifier.padding(Dimens.MarginSmall8),
        text = stringResource(R.string.favorite),
        style = Typography.TitleStyle,
    )

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
                imageSize = imageSize,
                onToggleFavorite = onToggleFavorite,
                onAddToCart = onAddToCart,
                onRemoveFromCart = onRemoveFromCart,
                onMealDetailsClick = onMealDetailsClick,
            )
        }
    }
}

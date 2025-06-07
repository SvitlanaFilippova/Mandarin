package com.mandarinkafe.mandarin.shared.cart.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.Constants

@Composable
fun CartRecommendsList(
    recommendsList: List<CustomizedMeal>,
    onAddToCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
    modifier: Modifier,
) {

    LazyRow(
        modifier = modifier
            .padding(horizontal = Dimens.MarginSuperSmall4)
            .background(Colors.Transparent),
    ) {
        items(
            items = recommendsList,
            key = { it.id }
        ) { item ->
            CartRecommendsItemCard(
                modifier = Modifier.animateItem(tween(Constants.ANIMATION_DURATION_FAST)),
                item = item,
                onAddToCart = onAddToCart,
                onMealDetailsClick = onMealDetailsClick,
            )
        }
    }
}
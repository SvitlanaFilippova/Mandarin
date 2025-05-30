package com.mandarinkafe.mandarin.shared.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

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
        itemsIndexed(recommendsList) { _, item ->

            CartRecommendsItemCard(
                item = item,
                onAddToCart = onAddToCart,
                onMealDetailsClick = onMealDetailsClick,
            )
        }
    }
}
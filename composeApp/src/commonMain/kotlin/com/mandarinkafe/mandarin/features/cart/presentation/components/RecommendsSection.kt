package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RecommendsSection(
    mainRecommends: List<Meal>,
    separateRecommends: List<Meal>,
    recommendsAreLoading: Boolean,
    onAddToCart: (CartItem) -> Unit,
    onMealDetailsClick: (CartItem) -> Unit,
) {
    if (recommendsAreLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16),
            contentAlignment = Alignment.Center
        ) {
            MyCircularProgressIndicator(
                strokeWidth = Dimens.ProgressBarStroke6,
            )
        }
    } else {
        CartRecommendsList(
            recommendsList = mainRecommends,
            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
            onAddToCart = { onAddToCart(it.toCartItem()) },
            onMealDetailsClick = { onMealDetailsClick(it.toCartItem()) },
        )
    }

    // Сообщение про соевый соус и тд
    if (separateRecommends.isNotEmpty()) {
        TooltipText(
            modifier = Modifier.padding(Dimens.MarginSmall8),
            text = stringResource(MR.strings.sushi_soy_souse_tooltip)
        )
        CartRecommendsList(
            recommendsList = separateRecommends,
            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
            onAddToCart = { onAddToCart(it.toCartItem()) },
            onMealDetailsClick = { onMealDetailsClick(it.toCartItem()) },
        )
    }
}

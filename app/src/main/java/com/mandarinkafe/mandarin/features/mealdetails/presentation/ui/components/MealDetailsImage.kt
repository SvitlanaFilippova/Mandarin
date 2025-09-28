package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.ui.components.LabelChip
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealImagePlaceholder
import com.mandarinkafe.mandarin.util.presentation.ui.components.NoDeliveryChip

@Composable
fun MealDetailsImage(meal: Meal) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.White),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(meal.imageUrl)
                .crossfade(true)
                .allowHardware(false)
                .build(),
            contentDescription = stringResource(
                R.string.picture_of_meal_template,
                meal.name
            ),

            loading = { MealImagePlaceholder() },

            error = { MealImagePlaceholder() },

            success = { state ->
                val ratio = state.painter.intrinsicSize.width / state.painter.intrinsicSize.height
                val contentScale = if (ratio in 0.75f..1.5f) {
                    ContentScale.Crop // «нормальная» картинка, без рамки
                } else {
                    ContentScale.Fit // слишком узкая/высокая, вписываем в квадрат
                }

                SubcomposeAsyncImageContent(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            },
        )

        // ярлыки
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.MarginStandard16)
        ) {
            meal.labels.forEach {
                LabelChip(
                    label = it.toUiModel(),
                    size = LabelSize.BIG,
                )
            }
        }

        if (meal.isPickupOnly) {
            NoDeliveryChip(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Dimens.MarginStandard16),
                size = LabelSize.BIG,
            )
        }
    }
}

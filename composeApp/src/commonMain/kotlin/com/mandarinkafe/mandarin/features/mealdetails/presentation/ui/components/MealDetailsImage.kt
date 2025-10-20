package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.ui.components.KamelSubcomposeAsyncImage
import com.mandarinkafe.mandarin.util.presentation.ui.components.LabelChip
import com.mandarinkafe.mandarin.util.presentation.ui.components.NoDeliveryChip
import dev.icerock.moko.resources.compose.stringResource

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
        KamelSubcomposeAsyncImage(
            model = meal.imageUrl,
            previewModel = meal.imagePreviewUrl,
            contentDescription = stringResource(MR.strings.picture_of_meal_template, meal.name),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            placeholder = MR.images.placeholder_meal_no_photo,
            error = MR.images.placeholder_meal_no_photo,
            crossfade = true
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

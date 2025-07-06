package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.LabelChip
import com.mandarinkafe.mandarin.util.presentation.ui.components.NoDeliveryChip

@Composable
fun MealDetailsImage(meal: Meal) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = meal.imageUrl,
            contentDescription = stringResource(
                R.string.picture_of_meal_template,
                meal.name
            ),
            error = painterResource(R.drawable.placeholder_meal_no_photo),
            placeholder = painterResource(R.drawable.placeholder_meal_no_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(vertical = Dimens.MarginSmall8)
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
        )

        // Ярлыки
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
                    cardIsSmall = false,
                )
            }
        }

        // Метка "только самовывоз"
        if (meal.isPickupOnly) {
            NoDeliveryChip(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Dimens.MarginStandard16),
                cardIsSmall = false,
            )
        }
    }
}
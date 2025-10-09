package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.FavoriteButton
import io.github.aakira.napier.Napier

@Composable
fun MealItemImageBox(
    modifier: Modifier = Modifier,
    meal: Meal,
    cardIsSmall: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    labelSize: LabelSize
) {
    val spacerSize = if (cardIsSmall) Dimens.MarginSuperSmall2 else Dimens.MarginSuperSmall4
    val paddingSize = if (cardIsSmall) Dimens.MarginSuperSmall4 else Dimens.MarginSmall8
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.White),
        contentAlignment = Alignment.Center

    ) {
        KamelSubcomposeAsyncImage(
            model = meal.imageUrl,
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.placeholder_meal_no_photo),
            error = painterResource(R.drawable.placeholder_meal_no_photo),
            crossfade = true,
            onStateChange = { resource ->
                Napier.d("Meal image state for ${meal.name}: $resource")
            }
        )

        // Тэги блюда
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(spacerSize),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = paddingSize)
        ) {
            meal.labels.forEach {
                LabelChip(
                    label = it.toUiModel(),
                    size = labelSize,
                )
            }
        }

        // Метка "только самовывоз"
        if (meal.isPickupOnly) {
            NoDeliveryChip(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = paddingSize),
                size = labelSize,
            )
        }

        FavoriteButton(
            modifier = Modifier
                .align(Alignment.TopStart),
            isFavorite = isFavorite,
            onClick = onToggleFavorite
        )
    }
}
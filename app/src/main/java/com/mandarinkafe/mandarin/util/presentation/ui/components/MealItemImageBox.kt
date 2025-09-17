package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.mandarinkafe.mandarin.util.Constants.IMAGE_SIZE_IN_MENU
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.FavoriteButton

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
            .clip(RoundedCornerShape(Dimens.CornerRadius8)),
        contentAlignment = Alignment.Center

    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(meal.imageUrl)
                .size(IMAGE_SIZE_IN_MENU)
                .crossfade(true)
                .allowHardware(false)
                .build(),
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            modifier = Modifier.fillMaxSize(),

            loading = { Placeholder() },
            error = { Placeholder() },
            success = { state ->
                val ratio = state.painter.intrinsicSize.width / state.painter.intrinsicSize.height
                val contentScale = if (ratio in 0.75f..1.5f) {
                    ContentScale.Crop
                } else {
                    ContentScale.Fit
                }

                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(Dimens.CornerRadius8))
                        .background(Colors.White),
                    contentScale = contentScale
                )
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

@Composable
private fun Placeholder() {
    Image(
        painter = painterResource(R.drawable.placeholder_meal_no_photo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack),
        contentScale = ContentScale.Crop
    )
}
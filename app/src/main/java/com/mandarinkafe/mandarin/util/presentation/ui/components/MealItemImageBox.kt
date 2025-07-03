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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.search.presentation.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.Constants.IMAGE_SIZE_IN_MENU
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.FavoriteButton

@Composable
fun MealItemImageBox(
    modifier: Modifier = Modifier,
    meal: Meal,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(meal.imageUrl)
                .size(IMAGE_SIZE_IN_MENU) // Сжимаем до нужного размера
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
                .background(Colors.AppBlack),
            onState = {
                isLoading = it is AsyncImagePainter.State.Loading
                isError = it is AsyncImagePainter.State.Error
            }
        ) {
            when {
                isLoading || isError -> {
                    Image(
                        painter = painterResource(R.drawable.placeholder_meal_no_photo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                    )
                }

                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
        // Тэги блюда
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.MarginSmall8)
        ) {
            meal.labels.forEach {
                LabelChip(
                    label = it.toUiModel(),
                )
            }
        }

        FavoriteButton(
            modifier = Modifier
                .align(Alignment.TopStart),
            isFavorite = isFavorite,
            onClick = onToggleFavorite
        )
    }
}
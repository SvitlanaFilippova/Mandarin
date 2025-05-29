package com.mandarinkafe.mandarin.util.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.ui.components.buttons.FavoriteButton

@Composable
fun MealItemImageBox(
    modifier: Modifier = Modifier,
    meal: Meal,
    onToggleFavorite: (Meal) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        SubcomposeAsyncImage(
            model = meal.imageUrl,
            contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimens.CornerRadius8))
                .background(
                    color = Colors.AppBlack,
                    shape = RoundedCornerShape(Dimens.CornerRadius8)
                )
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .padding(Dimens.MarginSmall8),
                        color = Colors.GreyTransparent75,
                        strokeWidth = Dimens.PhotoPlaceholderStrokeWidth4
                    )
                }

                is AsyncImagePainter.State.Error -> {
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
            isFavorite = meal.isFavorite,
            onClick = { onToggleFavorite(meal) }
        )
    }
}
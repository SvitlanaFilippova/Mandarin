package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.IMAGE_SIZE_IN_MENU
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealImagePlaceholder

@Composable
fun CartRecommendsItemCard(
    modifier: Modifier,
    meal: Meal,
    onAddToCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
) {
    Card(
        modifier = modifier
            .padding(horizontal = Dimens.MarginSuperSmall4)
            .width(Dimens.RecommendsItemWidth96)
            .clickable(onClick = { onMealDetailsClick(meal) }),
        border = BorderStroke(
            width = Dimens.Border1,
            color = Colors.DarkGrey
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Colors.AppBlack)
                .padding(Dimens.MarginSmall8)

        ) {
            // Изображение блюда
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(meal.imageUrl)
                    .size(IMAGE_SIZE_IN_MENU)
                    .crossfade(true)
                    .allowHardware(false)
                    .build(),
                contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
                modifier = Modifier.size(Dimens.MealSmallImage80),

                loading = { MealImagePlaceholder() },
                error = { MealImagePlaceholder() },
                success = { state ->
                    val ratio =
                        state.painter.intrinsicSize.width / state.painter.intrinsicSize.height
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
            // Название блюда
            Text(
                text = meal.name,
                style = Typography.SmallTextStyle,
                overflow = TextOverflow.Ellipsis,
                minLines = 2,
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.MarginSmall8)
            )

            if (meal.requireSelection) {
                SelectSmallButton(
                    onClick = { onMealDetailsClick(meal) },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                ToCartSmallButton(
                    onClick = { onAddToCart(meal) },
                    price = meal.price,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

    }
}

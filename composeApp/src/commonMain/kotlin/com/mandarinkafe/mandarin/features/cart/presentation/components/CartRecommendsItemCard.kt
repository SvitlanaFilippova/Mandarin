package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.KamelSubcomposeAsyncImage

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
            KamelSubcomposeAsyncImage(
                modifier = Modifier
                    .size(Dimens.MealSmallImage80)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(Dimens.CornerRadius8)),
                model = meal.imagePreviewUrl,
                previewModel = meal.placeholderUrl,
                contentDescription = "Изображение ${meal.name}",
                placeholder = MR.images.placeholder_meal_no_photo,
                error = MR.images.placeholder_meal_no_photo,
                crossfade = true
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

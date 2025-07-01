package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun CartRecommendsItemCard(
    modifier: Modifier,
    item: CustomizedMeal,
    onAddToCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
) {
    val meal = item.meal

    Card(
        modifier = modifier
            .padding(horizontal = Dimens.MarginSuperSmall4)
            .width(Dimens.RecommendsItemWidth96)
            .clickable(onClick = { onMealDetailsClick(item) }),
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
            AsyncImage(
                model = meal.imageUrl.ifEmpty { R.drawable.placeholder_meal_no_photo },
                contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(Dimens.MealSmallImage80)
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
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

            if (item.meal.requireSelection) {
                SelectSmallButton(
                    onClick = { onMealDetailsClick(item) },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                ToCartSmallButton(
                    onClick = { onAddToCart(item) },
                    price = meal.price,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

    }
}

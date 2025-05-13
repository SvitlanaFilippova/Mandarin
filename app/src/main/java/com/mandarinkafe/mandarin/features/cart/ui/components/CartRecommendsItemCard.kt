package com.mandarinkafe.mandarin.features.cart.ui.components

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
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract

@Composable
fun CartRecommendsItemCard(
    item: CartItem,
    onEvent: (CartContract.Event) -> Unit
) {
    val meal = item.meal

    Card(
        modifier = Modifier
            .padding(horizontal = Dimens.MarginSmall8)
            .width(Dimens.RecommendsItemWidth96)
            .clickable(onClick = { onEvent(CartContract.Event.OpenMealDetails(item)) }),
        border = BorderStroke(
            width = Dimens.RecommendsCardBorder1,
            color = Colors.GreyTransparent10
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
                model = meal.imageUrl.ifEmpty { R.drawable.logo_orange_square },
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

            when (item.meal.editableType) {
                EditableType.MODIFIABLE, EditableType.WOK -> SelectSmallButton(
                    onClick = { onEvent(CartContract.Event.OpenMealDetails(item)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                else -> ToCartSmallButton(
                    onClick = { onEvent(CartContract.Event.AddToCart(item)) },
                    price = meal.price,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

        }
    }

}

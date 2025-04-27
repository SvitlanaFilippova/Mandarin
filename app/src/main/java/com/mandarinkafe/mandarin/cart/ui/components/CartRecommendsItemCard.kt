package com.mandarinkafe.mandarin.cart.ui.components

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
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.util.applyTypography

@Preview
@Composable
fun PreviewCartRecommendsItemCard() {
    val mockMeal = Meal(
        id = "232",
        name = "Газированный напиток Добрый Cola без сахара, 250 мл".applyTypography(),
        weight = 0,
        price = 999,
        imageUrl = "https://cdn1.ozone.ru/s3/multimedia-1-n/c600/7122445511.jpg",
        isFavorite = false,
        description = "",
        tags = emptyList(),
        labels = emptyList(),
        isHidden = false,
        editableType = null,
        modifiers = emptyList(),
        adds = emptyList()
    )
    CartRecommendsItemCard(
        item = CartItem(
            meal = mockMeal,
            quantity = 0
        ),
        onEvent = { CartContract.Event.AddToCart(mockMeal) }
    )
}

@Composable
fun CartRecommendsItemCard(
    item: CartItem,
    onEvent: (CartContract.Event) -> Unit
) {
    val meal = item.meal
    val isInCart = item.quantity > 0
    Card(
        modifier = Modifier
            .padding(horizontal = Dimens.MarginSmall8)
            .width(Dimens.RecommendsItemWidth96)
            .clickable(onClick = { onEvent(CartContract.Event.OpenMealDetails(meal)) }),
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


            ToCartSmallButton(
                isInCart = isInCart,
                onClick = { onEvent(CartContract.Event.AddToCart(meal)) },
                price = meal.price,
                modifier = Modifier.fillMaxWidth(),
            )

        }
    }

}


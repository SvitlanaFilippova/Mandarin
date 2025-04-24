package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Composable
fun CartControls(
    numberInCart: Int,
    totalPrice: Int,
    meal: Meal,
    onEvent: (CartContract.Event) -> Unit
) {
    Box(
        modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall32)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.GreyTransparent10)
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Кнопка "-"
            IconButton(
                onClick = { onEvent(CartContract.Event.RemoveFromCart(meal)) },
                modifier = Modifier.size(Dimens.ButtonToCartSmall32)
            ) {
                Text(
                    stringResource(id = R.string.minus),
                    style = Typography.ToCartButtonStyle,
                    color = Color.White
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.meal_in_cart_count_template, numberInCart),
                    style = Typography.CartButtonSmallTextStyle
                )
                Text(
                    stringResource(R.string.meal_price_template, numberInCart * totalPrice),
                    style = Typography.CartButtonSmallTextStyle,
                    color = Colors.WhiteTransparent75
                )
            }

            // Кнопка "+"
            IconButton(
                onClick = { onEvent(CartContract.Event.AddToCart(meal)) },
                modifier = Modifier.size(Dimens.ButtonToCartSmall32)
            ) {
                Text(
                    stringResource(id = R.string.plus),
                    style = Typography.ToCartButtonStyle,
                    color = Color.White
                )
            }
        }
    }
}
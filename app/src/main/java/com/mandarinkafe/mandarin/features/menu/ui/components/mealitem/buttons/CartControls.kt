package com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

/**
 * Блок должен появляться,когда блюдо в корзне. Содержит кнопки +, -, количество и общую стоимость.
 */

@Composable
fun CartControls(
    numberInCart: Int,
    totalPrice: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(Dimens.ButtonToCartSmall32)
            .widthIn(min = Dimens.ButtonToCartBig120)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.Orange.copy(alpha = 0.20f))
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Кнопка "-"
            IconButton(
                onClick = onDecrease,
                modifier = Modifier.size(Dimens.ButtonToCartSmall32)
            ) {
                // Если последний экземпляр в корзине, то кнопка меняется на "урну"
                if (numberInCart == 1) {
                    Icon(
                        modifier = Modifier.padding(Dimens.MarginSmall8),
                        imageVector = Icons.Default.Delete,
                        tint = Color.White.copy(alpha = 0.75f),
                        contentDescription = stringResource(id = R.string.remove_from_cart),
                    )
                } else if (numberInCart > 1) {
                    Text(
                        stringResource(id = R.string.minus),
                        style = Typography.ToCartButtonBigStyle,
                        color = Color.White
                    )
                }
            }

            // Информация о количестве блюда в корзине и их сумме
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.meal_in_cart_count_template, numberInCart),
                    style = Typography.CartButtonSmallTextStyle
                )
                Text(
                    stringResource(R.string.meal_price_template, totalPrice),
                    style = Typography.CartButtonSmallTextStyle,
                    color = Colors.WhiteTransparent75
                )
            }

            // Кнопка "+"
            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(Dimens.ButtonToCartSmall32)
            ) {
                Text(
                    stringResource(id = R.string.plus),
                    style = Typography.ToCartButtonBigStyle,
                    color = Color.White
                )
            }
        }
    }
}
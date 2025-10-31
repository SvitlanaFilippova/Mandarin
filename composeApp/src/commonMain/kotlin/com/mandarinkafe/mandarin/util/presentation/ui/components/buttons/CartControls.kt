package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

/**
 * Блок должен появляться,когда блюдо в корзне. Содержит кнопки +, -, количество и общую стоимость.
 */

@Composable
fun CartControls(
    modifier: Modifier = Modifier,
    numberInCart: Double,
    totalPrice: Double,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.OrangeTransparent20)
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Кнопка "-"
            IconButton(
                onClick = onDecrease,
                modifier = Modifier.size(Dimens.ButtonToCartSmall36)
            ) {
                // Если последний экземпляр в корзине, то кнопка меняется на "урну"
                if (numberInCart <= 1.0) {
                    Icon(
                        modifier = Modifier.padding(Dimens.MarginSmall8),
                        painter = painterResource(MR.images.ic_delete),
                        tint = Color.White.copy(alpha = 0.75f),
                        contentDescription = stringResource(MR.strings.remove_from_cart),
                    )
                } else if (numberInCart > 1.0) {
                    Text(
                        stringResource(MR.strings.minus),
                        style = Typography.ToCartButtonBigStyle,
                        color = Color.White
                    )
                }
            }

            // Информация о количестве блюда в корзине и их сумме
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(MR.strings.meal_in_cart_count_template, numberInCart.toInt()),
                    style = Typography.CartButtonSmallTextStyle
                )
                Text(
                    stringResource(MR.strings.meal_price_template, totalPrice.toInt()),
                    style = Typography.CartButtonSmallTextStyle,
                    color = Colors.WhiteTransparent75
                )
            }

            // Кнопка "+"
            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(Dimens.ButtonToCartSmall36)
            ) {
                Text(
                    stringResource(MR.strings.plus),
                    style = Typography.ToCartButtonBigStyle,
                    color = Color.White
                )
            }
        }
    }
}
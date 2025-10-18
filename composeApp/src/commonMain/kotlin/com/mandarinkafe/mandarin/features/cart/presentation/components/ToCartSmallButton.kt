package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
// TODO: Добавить иконки для KMP
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun ToCartSmallButton(
    modifier: Modifier = Modifier,
    price: Int,
    onClick: () -> Unit,
    isInCart: Boolean = false,
) {
    Button(
        modifier = modifier.heightIn(min = Dimens.ButtonToCartSmall36),
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSuperSmall4),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.OrangeTransparent20,
            contentColor = Color.White
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            if (!isInCart) {
                Icon(
                    painter = painterResource(MR.images.ic_cart),
                    contentDescription = stringResource(MR.strings.add_to_cart),
                    modifier = Modifier.size(Dimens.IconSize20),
                    tint = Color.White
                )
                Text(
                    text = stringResource(MR.strings.meal_price_template, price),
                    style = Typography.ToCartButtonStyle
                )
            } else {
                Icon(
                    painter = painterResource(MR.images.ic_check),
                    contentDescription = stringResource(MR.strings.added_to_cart),
                    modifier = Modifier.size(Dimens.IconSize20),
                    tint = Color.White
                )
            }
        }
    }
}

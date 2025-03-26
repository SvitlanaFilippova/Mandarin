package com.mandarinkafe.mandarin.menu.ui.components.buttons

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
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun CartControls(numberInCart: Int, price: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Box(
        modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall32)
            .clip(RoundedCornerShape(Dimens.ButtonRadius8))
            .background(Colors.GreyTransparent10)
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(Dimens.ButtonToCartSmall32)) {
                Text("-", style = Typography.ToCartButtonStyle, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$numberInCart шт", style = Typography.CartButtonSmallTextStyle)
                Text(
                    "${numberInCart * price} ₽",
                    style = Typography.CartButtonSmallTextStyle,
                    color = Colors.WhiteTransparent75
                )
            }
            IconButton(onClick = onIncrease, modifier = Modifier.size(Dimens.ButtonToCartSmall32)) {
                Text("+", style = Typography.ToCartButtonStyle, color = Color.White)
            }
        }
    }
}
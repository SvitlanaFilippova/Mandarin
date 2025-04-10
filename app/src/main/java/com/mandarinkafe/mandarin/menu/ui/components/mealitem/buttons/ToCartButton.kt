package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun ToCartButton(price: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Orange,
            contentColor = Color.White
        ),
        modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall32)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cart),
                contentDescription = stringResource(id = R.string.add_to_cart),
                tint = Color.White
            )
            Text(
                stringResource(id = R.string.meal_price_template, price),
                style = Typography.ToCartButtonStyle
            )
        }
    }
}

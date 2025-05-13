package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
fun ToCartSmallButton(
    modifier: Modifier = Modifier,
    price: Int,
    onClick: () -> Unit,
    isInCart: Boolean = false,
    ) {
    Button(
        modifier = modifier.height(Dimens.ButtonToCartSmall32),
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSuperSmall4),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Orange.copy(alpha = 0.20f),
            contentColor = Color.White
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {
            if (!isInCart) {
                Icon(
                    modifier = Modifier.weight(0.3f),
                    painter = painterResource(R.drawable.ic_cart),
                    contentDescription = stringResource(id = R.string.add_to_cart),
                    tint = Color.White
                )
                Text(
                    text = stringResource(id = R.string.meal_price_template, price),
                    modifier = Modifier.weight(0.7f),
                    style = Typography.ToCartButtonStyle
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(id = R.string.added_to_cart),
                    tint = Color.White
                )
            }

        }
    }
}
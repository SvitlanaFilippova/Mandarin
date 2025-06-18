package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun ProcessOrderButton(
    onClick: () -> Unit,
    totalPrice: Int,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Orange,
            contentColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MarginSmall8)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {

            Text(
                text = stringResource(R.string.process_order_price_template, totalPrice),
                style = Typography.ToCartButtonBigStyle
            )
        }
    }
}
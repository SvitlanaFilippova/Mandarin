package com.mandarinkafe.mandarin.features.meal_details.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
fun ToCartButton(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    shouldBeActive: Boolean,
    totalPrice: Int,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
) {
    val contentColor = if (shouldBeActive) {
        Color.White
    } else {
        Color.White.copy(alpha = 0.5f)
    }
    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = if (isEditMode) onEdit else onAddToCart,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        enabled = shouldBeActive,
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Orange.copy(alpha = 0.95f),
            disabledContainerColor = Colors.LightGrey.copy(alpha = 0.4f),
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cart),
                contentDescription = stringResource(id = R.string.add_to_cart),
                tint = contentColor
            )
            Text(
                text = if (isEditMode) stringResource(
                    R.string.save_meal,
                    totalPrice
                ) else stringResource(R.string.meal_price_template, totalPrice),
                style = Typography.ToCartButtonBigStyle,
                color = contentColor

            )
        }
    }
}
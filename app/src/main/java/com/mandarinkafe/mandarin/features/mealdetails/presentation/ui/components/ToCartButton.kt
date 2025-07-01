package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

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
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun ToCartButton(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    shouldBeActive: Boolean,
    totalPrice: Int,
    onMissingRequiredOptions: () -> Unit,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
) {
    val contentColor = if (shouldBeActive) {
        Color.White
    } else {
        Color.White.copy(alpha = 0.5f)
    }
    val containerColor = if (shouldBeActive) {
        Colors.Orange.copy(alpha = 0.95f)
    } else {
        Colors.LightGrey.copy(alpha = 0.4f)
    }

    val onClickAction = when {
        !shouldBeActive -> onMissingRequiredOptions
        isEditMode -> onEdit
        else -> onAddToCart
    }

    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClickAction,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
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
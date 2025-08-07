package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MyCircularProgressIndicator

@Composable
fun SubmitOrderButton(
    modifier: Modifier = Modifier,
    shouldBeActive: Boolean,
    totalOrderSum: Double,
    onMissingRequiredInfo: () -> Unit,
    onSubmitOrder: () -> Unit,
    isLoading: Boolean,
) {
    val contentColor = if (shouldBeActive) {
        Color.White
    } else {
        Color.White.copy(alpha = 0.5f)
    }
    val containerColor = if (shouldBeActive) {
        Colors.Orange
    } else {
        Colors.LightGrey.copy(alpha = 0.4f)
    }

    val onClickAction = when {
        !shouldBeActive -> onMissingRequiredInfo
        else -> onSubmitOrder
    }

    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.BigButtonWithTextHeight),
        onClick = onClickAction,
        enabled = !isLoading,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {
            Text(
                text = stringResource(R.string.submit_order),
                style = Typography.ToCartButtonBigStyle,
                color = contentColor
            )
            Spacer(modifier = Modifier.weight(1f))
            when (isLoading) {
                true -> {
                    MyCircularProgressIndicator(
                        strokeWidth = Dimens.ProgressBarStroke6,
                    )
                }

                false -> {
                    Text(
                        text = stringResource(R.string.float_price_template, totalOrderSum),
                        style = Typography.ToCartButtonBigStyle,
                        color = contentColor
                    )
                }
            }

        }
    }
}
package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderSummaryRow(
    name: String,
    amount: Float?,
    color: Color? = null,
    hintText: String? = null,
    inProgress: Boolean = false,
) {
    val finalTextStyle = if (color == null) {
        Typography.RegularLightTextStyle
    } else {
        Typography.RegularLightTextStyle.copy(color = color)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.OrderSummaryRowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = finalTextStyle)

        if (hintText != null) {
            IconWithTooltipInfo(hintText)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (inProgress) {
            CircularProgressIndicator(color = Colors.LightGrey)
        }

        if (amount != null && !inProgress) {
            Text(
                text = stringResource(MR.strings.float_price_template, amount),
                style = finalTextStyle
            )

        } else {
            IconWithTooltipInfo(stringResource(MR.strings.delivery_request_validation))
        }

    }
}
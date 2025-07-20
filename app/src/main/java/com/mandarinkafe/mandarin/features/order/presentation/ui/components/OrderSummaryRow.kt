package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun OrderSummaryRow(name: String, amount: Float, hintResId: Int? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.OrderSummaryRowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = Typography.RegularLightTextStyle)

        if (hintResId != null) {
            IconWithTooltipInfo(hintResId)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.order_total_cost_template, amount),
            style = Typography.RegularLightTextStyle
        )
    }
}
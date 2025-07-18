package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun OrderSummaryRow(name: String, amount: Int, hintResId: Int? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.OrderSummaryRowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = Typography.RegularLightTextStyle)

        if (hintResId != null) {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = stringResource(id = R.string.why_is_that_number),
                    tint = Colors.LightGrey,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.meal_price_template, amount),
            style = Typography.RegularLightTextStyle
        )
    }
}
package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.toUi

@Composable
fun HistoryOrderStatusSection(orderStatus: DeliveryStatus?, color: Color) {
    val orderUiStatus = remember(orderStatus) { orderStatus?.toUi() }
    orderUiStatus?.let {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                painterResource(orderUiStatus.iconResID),
                contentDescription = stringResource(orderUiStatus.nameRes),
                tint = color
            )
            Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
            Text(
                text = stringResource(orderUiStatus.nameRes),
                style = Typography.RegularTextStyle,
                color = color
            )
        }
    }
}

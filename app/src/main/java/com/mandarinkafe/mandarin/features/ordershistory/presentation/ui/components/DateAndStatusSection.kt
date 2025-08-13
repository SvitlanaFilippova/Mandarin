package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus.CANCELLED
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus.CLOSED
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus.UNCONFIRMED
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

@Composable
fun DateAndStatusSection(order: SavedOrder) {
    val color = remember(order.status) {
        when (order.status) {
            null -> Colors.White
            UNCONFIRMED -> Colors.White
            CLOSED -> Colors.Orange
            CANCELLED -> Colors.ErrorRed
            else -> Colors.Green
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = order.whenCreated,
            style = Typography.RegularTextStyle,
        )
        Spacer(modifier = Modifier.weight(1f))
        order.status?.let {
            Icon(
                painterResource(it.iconResID),
                contentDescription = stringResource(it.labelResId),
                tint = color
            )
            Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
            Text(
                text = stringResource(it.labelResId),
                style = Typography.RegularTextStyle,
                color = color
            )
        }
    }
    Spacer(modifier = Modifier.height(Dimens.MarginSuperSmall4))
    HorizontalDivider(
        modifier = Modifier.height(Dimens.DividerHeight1),
        color = color.copy(alpha = 0.5f)
    )
}
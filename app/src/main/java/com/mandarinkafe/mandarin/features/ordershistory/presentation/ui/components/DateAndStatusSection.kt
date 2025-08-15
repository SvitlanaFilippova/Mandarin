package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus

@Composable
fun DateAndStatusSection(orderStatus: DeliveryStatus?, whenCreated: String) {
    val color = remember(orderStatus) {
        when (orderStatus) {
            null -> Colors.White
            DeliveryStatus.UNCONFIRMED -> Colors.White
            DeliveryStatus.CLOSED -> Colors.Orange
            DeliveryStatus.CANCELLED -> Colors.ErrorRed
            else -> Colors.Green
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = whenCreated,
            style = Typography.RegularTextStyle,
        )
        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = orderStatus != null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            HistoryOrderStatusSection(
                orderStatus = orderStatus,
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
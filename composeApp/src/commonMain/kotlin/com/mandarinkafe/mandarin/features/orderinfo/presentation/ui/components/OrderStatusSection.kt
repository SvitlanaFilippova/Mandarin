package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderStatusSection(deliveryStatus: UiDeliveryStatus, shouldShowRefundText: Boolean) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(Dimens.MarginStandard16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconHuge),
                painter = painterResource(deliveryStatus.iconRes),
                contentDescription = null
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) { Value(stringResource(deliveryStatus.nameRes)) }

        deliveryStatus.extraTextResId?.let {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) { Label(stringResource(deliveryStatus.extraTextResId)) }
        }

        // Сообщение об отменённом заказе с успешной онлайн-оплатой
        if (shouldShowRefundText) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) { Label(stringResource(MR.strings.order_cancelled_but_paid_online)) }
        }
    }
}

package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderStatusSection(
    deliveryStatus: UiDeliveryStatus,
    shouldShowRefundText: Boolean,
    cancelComment: String? = null,
    isOnlinePayment: Boolean = false,
    isPaymentPaid: Boolean? = null,
) {
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

        // Для статуса UNCONFIRMED с онлайн-оплатой, если заказ не оплачен, показываем специальный текст
        val extraTextResId = if (
            deliveryStatus == UiDeliveryStatus.UNCONFIRMED &&
            isOnlinePayment &&
            isPaymentPaid != true
        ) {
            MR.strings.delivery_status_extra_unconfirmed_unpay
        } else {
            deliveryStatus.extraTextResId
        }

        extraTextResId?.let {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) { Label(stringResource(it)) }
        }

        cancelComment?.let { text ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(MR.strings.order_cancel_reason_template, text),
                    style = Typography.RegularLightTextStyle.copy(color = Colors.Red),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Dimens.MarginSmall8)
                )
            }
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

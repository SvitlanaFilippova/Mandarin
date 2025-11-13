package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PaymentInfoSection(paymentStatus: PaymentStatus?, isPaymentPaid: Boolean?) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
        ) {
            LabelValue(
                stringResource(MR.strings.label_payment_status),
                paymentStatus?.name ?: "Неизвестен"
            )


            if (isPaymentPaid != true) {
                ButtonWithText(
                    text = stringResource(MR.strings.submit_order_online),
                    onClick = {} // TODO кидать снова на оплату
                )
            }
        }

    }
}
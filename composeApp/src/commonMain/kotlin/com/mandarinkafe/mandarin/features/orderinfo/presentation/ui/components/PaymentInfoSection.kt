package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PaymentInfoSection(
    paymentStatus: PaymentStatus?,
    isPaymentPaid: Boolean?,
    isPaymentLoading: Boolean = false,
    isPaymentProcessing: Boolean = false,
    isPaymentPolling: Boolean = false,
    paymentError: StringResource? = null,
    onStartPayment: () -> Unit = {},
    onRetryPayment: () -> Unit = {},
) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {
            LabelValue(
                stringResource(MR.strings.label_payment_status),
                paymentStatus?.name ?: "Неизвестен"
            )

            // Показываем индикатор загрузки во время обработки платежа
            if (isPaymentLoading || isPaymentProcessing || isPaymentPolling) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
                ) {
                    CircularProgressIndicator(
                        color = Colors.Orange,
                        modifier = Modifier.padding(vertical = Dimens.MarginSmall8)
                    )
                    val loadingText = when {
                        isPaymentProcessing -> stringResource(MR.strings.payment_processing)
                        isPaymentPolling -> stringResource(MR.strings.payment_polling)
                        else -> stringResource(MR.strings.payment_processing)
                    }
                    LabelValue(
                        label = "",
                        value = loadingText
                    )
                }
            }

            // Показываем ошибку, если есть
            if (paymentError != null && !isPaymentLoading && !isPaymentProcessing && !isPaymentPolling) {
                LabelValue(
                    label = stringResource(MR.strings.label_error),
                    value = stringResource(paymentError)
                )
            }

            // Кнопка оплаты/повтора
            if (isPaymentPaid != true && !isPaymentLoading && !isPaymentProcessing && !isPaymentPolling) {
                ButtonWithText(
                    text = if (paymentError != null) {
                        stringResource(MR.strings.payment_retry)
                    } else {
                        stringResource(MR.strings.submit_order_online)
                    },
                    onClick = {
                        if (paymentError != null) {
                            onRetryPayment()
                        } else {
                            onStartPayment()
                        }
                    }
                )
            }
        }
    }
}
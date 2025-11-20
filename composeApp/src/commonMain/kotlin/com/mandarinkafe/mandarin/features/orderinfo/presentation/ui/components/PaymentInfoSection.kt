package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.features.payment.domain.models.toDisplayString
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_BANK_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_CASH_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PaymentInfoSection(
    paymentStatus: PaymentStatus?,
    isPaymentInProgress: Boolean,
    isPaymentProcessing: Boolean,
    isPaymentPolling: Boolean,
    canShowPaymentError: Boolean,
    canShowPaymentButton: Boolean,
    paymentError: StringResource? = null,
    paymentMethodCode: String? = null,
    onStartPayment: () -> Unit = {},
    onRetryPayment: () -> Unit = {},
) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PaymentMethodLabel(paymentMethodCode)
            // Показываем статус и элементы оплаты только для онлайн-оплат
            if (paymentStatus != null || isPaymentInProgress || paymentError != null) {
                PaymentStatusLabel(paymentStatus)
                PaymentProgressIndicator(
                    isPaymentInProgress = isPaymentInProgress,
                    isPaymentProcessing = isPaymentProcessing,
                    isPaymentPolling = isPaymentPolling
                )
                PaymentErrorLabel(
                    canShowError = canShowPaymentError,
                    paymentError = paymentError
                )
                PaymentButton(
                    canShowButton = canShowPaymentButton,
                    paymentError = paymentError,
                    onStartPayment = onStartPayment,
                    onRetryPayment = onRetryPayment
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodLabel(paymentMethodCode: String?) {
    paymentMethodCode?.let { code ->
        val paymentMethodDisplay = getPaymentMethodDisplayString(code)
        if (paymentMethodDisplay != null) {
            LabelValue(
                stringResource(MR.strings.payment_type).replace(" *", ""),
                paymentMethodDisplay
            )
        }
    }
}

@Composable
private fun PaymentStatusLabel(paymentStatus: PaymentStatus?) {
    LabelValue(
        stringResource(MR.strings.label_payment_status),
        paymentStatus?.let { stringResource(it.toDisplayString()) }
            ?: stringResource(MR.strings.payment_status_unknown)
    )
}

private fun getPaymentMethodDisplayString(code: String?): String? {
    if (code == null) return null
    return when (code.uppercase()) {
        PAYMENT_CASH_CODE -> "Наличными"
        PAYMENT_BANK_CODE -> "Картой при получении"
        PAYMENT_ONLINE_CODE -> "Онлайн-оплата"
        else -> null
    }
}

@Composable
private fun PaymentProgressIndicator(
    isPaymentInProgress: Boolean,
    isPaymentProcessing: Boolean,
    isPaymentPolling: Boolean,
) {
    if (isPaymentInProgress) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyCircularProgressIndicator(
                strokeWidth = Dimens.ProgressBarStroke6,
                modifier = Modifier.size(Dimens.ProgressBarSmallSize)
            )
            val loadingText = when {
                isPaymentProcessing -> stringResource(MR.strings.payment_processing)
                isPaymentPolling -> stringResource(MR.strings.payment_polling)
                else -> stringResource(MR.strings.payment_processing)
            }
            Value(text = loadingText)
        }
    }
}

@Composable
private fun PaymentErrorLabel(
    canShowError: Boolean,
    paymentError: StringResource?,
) {
    if (canShowError && paymentError != null) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Label(
                text = stringResource(MR.strings.label_error) + ": " + stringResource(
                    paymentError
                )
            )
        }
    }
}

@Composable
private fun PaymentButton(
    canShowButton: Boolean,
    paymentError: StringResource?,
    onStartPayment: () -> Unit,
    onRetryPayment: () -> Unit,
) {
    if (canShowButton) {
        ButtonWithText(
            modifier = Modifier.fillMaxWidth(),
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
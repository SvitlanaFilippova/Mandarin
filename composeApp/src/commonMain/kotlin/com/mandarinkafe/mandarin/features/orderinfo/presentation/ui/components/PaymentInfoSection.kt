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
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
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
    val paymentState = calculatePaymentState(
        isPaymentLoading,
        isPaymentProcessing,
        isPaymentPolling,
        isPaymentPaid,
        paymentStatus
    )

    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PaymentStatusLabel(paymentStatus)
            PaymentProgressIndicator(
                isPaymentInProgress = paymentState.isPaymentInProgress,
                isPaymentProcessing = isPaymentProcessing,
                isPaymentPolling = isPaymentPolling
            )
            PaymentErrorLabel(
                canShowError = paymentState.canShowError,
                paymentError = paymentError
            )
            PaymentButton(
                canShowButton = paymentState.canShowButton,
                paymentError = paymentError,
                onStartPayment = onStartPayment,
                onRetryPayment = onRetryPayment
            )
        }
    }
}

private data class PaymentState(
    val isPaymentInProgress: Boolean,
    val canShowError: Boolean,
    val canShowButton: Boolean,
)

private fun calculatePaymentState(
    isPaymentLoading: Boolean,
    isPaymentProcessing: Boolean,
    isPaymentPolling: Boolean,
    isPaymentPaid: Boolean?,
    paymentStatus: PaymentStatus?,
): PaymentState {
    val isPaymentInProgress = isPaymentLoading || isPaymentProcessing || isPaymentPolling
    val canShowError = !isPaymentInProgress
    val canShowButton =
        isPaymentPaid != true && !isPaymentInProgress && paymentStatus != PaymentStatus.SUCCEEDED
    return PaymentState(isPaymentInProgress, canShowError, canShowButton)
}

@Composable
private fun PaymentStatusLabel(paymentStatus: PaymentStatus?) {
    LabelValue(
        stringResource(MR.strings.label_payment_status),
        paymentStatus?.let { stringResource(it.toDisplayString()) }
            ?: stringResource(MR.strings.payment_status_unknown)
    )
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
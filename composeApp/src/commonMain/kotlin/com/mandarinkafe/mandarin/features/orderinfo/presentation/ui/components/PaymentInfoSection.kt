package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.features.payment.domain.models.toDisplayString
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.toTimeFormat
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
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
    isChangingPaymentMethod: Boolean = false,
    paymentCanBeChanged: Boolean = false,
    availablePaymentTypes: List<PaymentType> = emptyList(),
    onStartPayment: () -> Unit = {},
    onRetryPayment: () -> Unit = {},
    onLoadPaymentTypes: () -> Unit = {}, // Загрузить доступные способы оплаты
    onChangePaymentMethod: (String) -> Unit = {},
    paymentTimeRemainingSeconds: Int? = null, // Оставшееся время на оплату в секундах
) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isChangingPaymentMethod) {
                // Показываем индикатор загрузки вместо способа оплаты
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Label(text = stringResource(MR.strings.payment_type).replace(": *", ""))

                    MyCircularProgressIndicator(
                        strokeWidth = Dimens.ProgressBarStroke6,
                        modifier = Modifier.size(Dimens.ProgressBarSmallSize)
                    )
                }
            } else {
                PaymentMethodSelector(
                    paymentMethodCode = paymentMethodCode,
                    paymentCanBeChanged = paymentCanBeChanged,
                    isChangingPaymentMethod = isChangingPaymentMethod,
                    availablePaymentTypes = availablePaymentTypes,
                    onLoadPaymentTypes = onLoadPaymentTypes,
                    onChangePaymentMethod = onChangePaymentMethod
                )
            }

            // Показываем статус оплаты для онлайн-оплат (включая отменённые заказы), если не идет изменение способа оплаты
            if (!isChangingPaymentMethod && paymentStatus != null) {
                PaymentStatusLabel(paymentStatus)
            }

            // Показываем таймер обратного отсчёта для онлайн-оплаты
            if (shouldShowPaymentTimer(
                    isChangingPaymentMethod,
                    paymentTimeRemainingSeconds,
                    paymentStatus
                )
            ) {
                PaymentTimer(remainingSeconds = paymentTimeRemainingSeconds!!)
            }

            // Показываем активные элементы оплаты (индикаторы, кнопки) только если не идет изменение способа оплаты
            if (!isChangingPaymentMethod && (isPaymentInProgress || paymentError != null || canShowPaymentButton)) {
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
private fun PaymentMethodSelector(
    paymentMethodCode: String?,
    paymentCanBeChanged: Boolean,
    isChangingPaymentMethod: Boolean,
    availablePaymentTypes: List<PaymentType>,
    onLoadPaymentTypes: () -> Unit,
    onChangePaymentMethod: (String) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedPaymentCode by remember { mutableStateOf<String?>(null) }

    paymentMethodCode
        ?.let { UiPaymentType.fromCode(it) }
        ?.let { uiPaymentType ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Label(text = stringResource(MR.strings.payment_type).replace(": *", ""))

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    if (paymentCanBeChanged && !isChangingPaymentMethod) {
                        PaymentMethodDropdown(
                            uiPaymentType = uiPaymentType,
                            availablePaymentTypes = availablePaymentTypes,
                            currentPaymentMethodCode = paymentMethodCode,
                            onLoadPaymentTypes = onLoadPaymentTypes,
                            onShowMenu = { showMenu = true },
                            onPaymentMethodSelected = { code ->
                                showMenu = false
                                if (code != paymentMethodCode) {
                                    selectedPaymentCode = code
                                    showConfirmationDialog = true
                                }
                            },
                            onDismissMenu = { showMenu = false },
                            showMenu = showMenu
                        )

                        PaymentMethodChangeDialog(
                            showDialog = showConfirmationDialog,
                            selectedPaymentCode = selectedPaymentCode,
                            onConfirm = { code ->
                                showConfirmationDialog = false
                                onChangePaymentMethod(code)
                                selectedPaymentCode = null
                            },
                            onDismiss = {
                                showConfirmationDialog = false
                                selectedPaymentCode = null
                            }
                        )
                    } else {
                        Value(stringResource(uiPaymentType.nameRes))
                    }
                }
            }
        }
}

@Composable
private fun PaymentMethodDropdown(
    uiPaymentType: UiPaymentType,
    availablePaymentTypes: List<PaymentType>,
    currentPaymentMethodCode: String?,
    onLoadPaymentTypes: () -> Unit,
    onShowMenu: () -> Unit,
    onPaymentMethodSelected: (String) -> Unit,
    onDismissMenu: () -> Unit,
    showMenu: Boolean,
) {
    var rowWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(modifier = Modifier.wrapContentSize()) {
        Row(
            modifier = Modifier
                .clickable(onClick = {
                    onLoadPaymentTypes()
                    onShowMenu()
                })
                .onGloballyPositioned { coordinates ->
                    rowWidth = with(density) { coordinates.size.width.toDp() }
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(uiPaymentType.nameRes),
                style = Typography.RegularTextStyle,
            )
            Icon(
                painterResource(MR.images.ic_arrow_drop_down),
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSize24),
                tint = Colors.WhiteTransparent75
            )
        }
        if (rowWidth > 0.dp) {
            HorizontalDivider(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(rowWidth)
                    .height(Dimens.DividerHeight1),
                color = Colors.WhiteTransparent75
            )
        }
    }

    ChangePaymentMethodMenu(
        availablePaymentTypes = availablePaymentTypes,
        currentPaymentMethodCode = currentPaymentMethodCode,
        onPaymentMethodSelected = onPaymentMethodSelected,
        onDismiss = onDismissMenu,
        expanded = showMenu && availablePaymentTypes.isNotEmpty()
    )
}

@Composable
private fun PaymentMethodChangeDialog(
    showDialog: Boolean,
    selectedPaymentCode: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog && selectedPaymentCode != null) {
        val selectedPaymentType = UiPaymentType.fromCode(selectedPaymentCode)
        selectedPaymentType?.let { paymentType ->
            ChangePaymentMethodConfirmationDialog(
                paymentMethodName = stringResource(paymentType.nameRes),
                onConfirm = { onConfirm(selectedPaymentCode) },
                onDismiss = onDismiss
            )
        }
    }
}


@Composable
private fun PaymentStatusLabel(paymentStatus: PaymentStatus?) {
    val statusColor = when (paymentStatus) {
        PaymentStatus.SUCCEEDED -> Colors.Green
        PaymentStatus.PENDING -> Colors.Orange
        PaymentStatus.UNKNOWN,
        null,
            -> Colors.Red

        PaymentStatus.CANCELED,
        PaymentStatus.REFUNDED,
            -> null

    }

    LabelValue(
        label = stringResource(MR.strings.label_payment_status),
        value = paymentStatus?.let { stringResource(it.toDisplayString()) }
            ?: stringResource(MR.strings.payment_status_unknown),
        valueColor = statusColor
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

@Composable
private fun PaymentTimer(remainingSeconds: Int) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Label(text = stringResource(MR.strings.payment_time_left))
            Text(
                text = remainingSeconds.toTimeFormat(),
                style = Typography.RegularLightTextStyle.copy(
                    color = if (remainingSeconds < 60) Colors.Red else Colors.White,
                    fontSize = Typography.TitleStyle.fontSize
                ),
                textAlign = TextAlign.End,
            )
        }
    }
}

private fun shouldShowPaymentTimer(
    isChangingPaymentMethod: Boolean,
    paymentTimeRemainingSeconds: Int?,
    paymentStatus: PaymentStatus?,
): Boolean {
    return !isChangingPaymentMethod &&
            paymentTimeRemainingSeconds != null &&
            paymentTimeRemainingSeconds > 0 &&
            paymentStatus != PaymentStatus.SUCCEEDED
}
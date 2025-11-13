package com.mandarinkafe.mandarin.features.payment.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration.Long
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEffect
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEvent
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberPaymentViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PaymentScreen(
    navController: NavController,
    orderId: String,
    amount: Double,
    userPhone: String,
) {
    val viewModel = rememberPaymentViewModel()
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val onEvent = viewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current
    var showCancelDialog by remember { mutableStateOf(false) }
    var pendingMessageRes: StringResource? by remember { mutableStateOf(null) }

    // Инициализация платежа при первом запуске
    LaunchedEffect(Unit) {
        Napier.d("PaymentFlow: [Screen] LaunchedEffect - calling SetInitData with orderId $orderId")
        onEvent(
            PaymentEvent.SetInitData(
                orderId = orderId,
                amount = amount,
                userPhone = userPhone
            )
        )
        Napier.d("PaymentFlow: [Screen] LaunchedEffect - calling InitPayment")
        onEvent(
            PaymentEvent.InitPayment
        )
    }

    // Обработка эффектов
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is PaymentEffect.PaymentSuccess -> {
                    navController.navigateToOrderInfo(
                        orderId = effect.orderId,
                        fromOrderCreation = true
                    )
                }

                is PaymentEffect.PaymentError -> {
                    pendingMessageRes = effect.message
                }

                is PaymentEffect.ShowCancelDialog -> {
                    showCancelDialog = true
                }

                is PaymentEffect.PaymentCanceled -> {
                    // TODO
                }
            }
        }
    }

    val pendingMessage: String? = pendingMessageRes?.let { stringResource(it) }

    LaunchedEffect(pendingMessage) {
        val msg = pendingMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = msg,
            duration = Long
        )
        pendingMessageRes = null
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.payment_screen_title),
            onBackClick = { navController.popBackStack() }
        )

        PaymentScreenContent(
            state = state,
            onEvent = onEvent,
            showCancelDialog = showCancelDialog,
            onDismissCancelDialog = { showCancelDialog = false },
            onConfirmCancel = {
                showCancelDialog = false
                onEvent(PaymentEvent.CancelPayment)
            }
        )
    }
}

@Composable
private fun PaymentScreenContent(
    state: PaymentContract.PaymentState,
    onEvent: (PaymentEvent) -> Unit,
    showCancelDialog: Boolean,
    onDismissCancelDialog: () -> Unit,
    onConfirmCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginStandard16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(color = Colors.Orange)
                Spacer(Modifier.height(Dimens.MarginStandard16))
                Text(
                    text = stringResource(MR.strings.payment_processing),
                    style = Typography.RegularTextStyle,
                    color = Colors.White
                )
            }

            state.isPaymentProcessing -> {
                CircularProgressIndicator(color = Colors.Orange)
                Spacer(Modifier.height(Dimens.MarginStandard16))
                Text(
                    text = stringResource(MR.strings.payment_processing),
                    style = Typography.RegularTextStyle,
                    color = Colors.White
                )
            }

            state.isPolling -> {
                CircularProgressIndicator(color = Colors.Orange)
                Spacer(Modifier.height(Dimens.MarginStandard16))
                Text(
                    text = stringResource(MR.strings.payment_polling),
                    style = Typography.RegularTextStyle,
                    color = Colors.White
                )
            }

            state.error != null -> {
                Text(
                    text = stringResource(state.error),
                    style = Typography.RegularTextStyle,
                    color = Colors.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Dimens.MarginStandard16)
                )
                Spacer(Modifier.height(Dimens.MarginStandard16))
                Button(
                    onClick = { onEvent(PaymentEvent.RetryPayment) },
                    colors = ButtonDefaults.buttonColors(containerColor = Colors.Orange)
                ) {
                    Text(
                        text = stringResource(MR.strings.payment_retry),
                        style = Typography.RegularTextStyle
                    )
                }
            }

            else -> {
                Text(
                    text = "Сумма к оплате: ${state.amount} ₽",
                    style = Typography.RegularTextStyle,
                    color = Colors.White
                )
            }
        }
    }

    // Диалог отмены платежа
    if (showCancelDialog) {
        PaymentCancelDialog(
            onDismiss = onDismissCancelDialog,
            onConfirm = onConfirmCancel
        )
    }
}

@Composable
private fun PaymentCancelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(MR.strings.payment_cancel_dialog_title),
                style = Typography.RegularTextStyle
            )
        },
        text = {
            Text(
                text = stringResource(MR.strings.payment_cancel_dialog_message),
                style = Typography.RegularTextStyle
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Colors.Red)
            ) {
                Text(
                    text = stringResource(MR.strings.payment_cancel_dialog_confirm),
                    style = Typography.RegularTextStyle
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Colors.Orange)
            ) {
                Text(
                    text = stringResource(MR.strings.payment_cancel_dialog_cancel),
                    style = Typography.RegularTextStyle
                )
            }
        }
    )
}


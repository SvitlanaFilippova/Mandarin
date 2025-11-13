package com.mandarinkafe.mandarin.features.payment.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.payment.YooKassaPaymentService
import com.mandarinkafe.mandarin.features.payment.domain.api.CancelPaymentUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.CreatePaymentUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.GetPaymentStatusUseCase
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEffect
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEffect.PaymentSuccess
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEvent
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.tickerFlow
import dev.icerock.moko.resources.StringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class PaymentViewModel(
    private val yooKassaService: YooKassaPaymentService,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val getPaymentStatusUseCase: GetPaymentStatusUseCase,
    private val cancelPaymentUseCase: CancelPaymentUseCase,
    private val orderId: String,
    private val amount: Double,
) : BaseViewModel<PaymentEvent, PaymentEffect, PaymentState>() {

    private var pollingJob: Job? = null
    private val maxPollingDuration = 60.seconds

    override fun setInitialState() =
        PaymentState(
            orderId = orderId,
            amount = amount
        )


    override fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.InitPayment -> initPayment()
            is PaymentEvent.RetryPayment -> retryPayment()
            is PaymentEvent.CancelPayment -> cancelPayment()
            is PaymentEvent.DismissError -> dismissError()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun initPayment() {
        viewModelScope.launch {
            setLoading(true)
            setState { copy(error = null) }

            // 1. Инициализация SDK и получение payment_token
            val sdkResult = yooKassaService.initializePayment(
                amount = state.value.amount.toFloat(),
                orderId = state.value.orderId
            )

            if (!sdkResult.success || sdkResult.paymentToken == null) {
                setLoading(false)
                setState {
                    copy(
                        error = MR.strings.error_payment_init_failed,
                        isLoading = false
                    )
                }
                sendErrorEffect(MR.strings.error_payment_init_failed)
                return@launch
            }

            val paymentToken = sdkResult.paymentToken

            // 2. Создание платежа на сервере
            val description = "Заказ №${state.value.orderId}"
            val createResult = createPaymentUseCase(
                paymentToken = paymentToken,
                orderId = state.value.orderId,
                amount = state.value.amount,
                currency = "RUB",
                description = description
            )

            when (createResult) {
                is Resource.Success -> {
                    val paymentInfo = createResult.data
                    if (paymentInfo == null) {
                        setLoading(false)
                        setState { copy(error = MR.strings.error_payment_creation_failed) }
                        sendErrorEffect(MR.strings.error_payment_creation_failed)
                        return@launch
                    }

                    val confirmationUrl = paymentInfo.confirmationUrl

                    // 3. Если есть confirmation_url, открываем форму оплаты
                    if (confirmationUrl != null) {
                        setState {
                            copy(
                                isLoading = false,
                                isPaymentProcessing = true,
                                confirmationUrl = confirmationUrl,
                                paymentStatus = paymentInfo.status
                            )
                        }

                        // Открываем форму оплаты через SDK
                        openPaymentForm(confirmationUrl)
                    } else {
                        // Если нет URL, сразу начинаем polling
                        setState {
                            copy(
                                isLoading = false,
                                paymentStatus = paymentInfo.status
                            )
                        }
                        startPolling()
                    }
                }

                is Resource.ErrorNoInternet -> {
                    setLoading(false)
                    setState { copy(error = MR.strings.error_no_internet) }
                    sendErrorEffect(MR.strings.error_no_internet)
                }

                else -> {
                    setLoading(false)
                    createResult.message ?: "Ошибка создания платежа"
                    setState { copy(error = MR.strings.error_payment_creation_failed) }
                    sendErrorEffect(MR.strings.error_payment_creation_failed)
                }
            }
        }
    }

    private suspend fun openPaymentForm(confirmationUrl: String) {
        val result = yooKassaService.openPaymentUrl(confirmationUrl)

        // После закрытия формы начинаем polling
        setState { copy(isPaymentProcessing = false) }

        if (result.success) {
            startPolling()
        } else {
            setState {
                copy(
                    error = MR.strings.error_payment_canceled,
                    isPaymentProcessing = false
                )
            }
            sendEffect(PaymentEffect.PaymentCanceled)
        }
    }

    private fun startPolling() {
        stopPolling()

        setState { copy(isPolling = true) }

        pollingJob = viewModelScope.launch {
            val isCompleted = withTimeoutOrNull(maxPollingDuration) {
                tickerFlow(period = 3.seconds)
                    .collect { _ ->
                        val statusResult = getPaymentStatusUseCase(state.value.orderId)

                        when (statusResult) {
                            is Resource.Success -> {
                                val paymentInfo = statusResult.data
                                setState { copy(paymentStatus = paymentInfo?.status) }
                                val status = paymentInfo?.status

                                when (status) {
                                    PaymentStatus.SUCCEEDED -> {
                                        stopPolling()
                                        sendEffect(PaymentSuccess(state.value.orderId))
                                    }

                                    PaymentStatus.CANCELED -> {
                                        stopPolling()
                                        setState { copy(error = MR.strings.error_payment_canceled) }
                                        sendEffect(PaymentEffect.PaymentCanceled)
                                    }

                                    PaymentStatus.PENDING, PaymentStatus.UNKNOWN, null -> {
                                        // Продолжаем polling
                                    }

                                }
                            }

                            is Resource.ErrorNoInternet -> {
                                stopPolling()
                                setState {
                                    copy(
                                        error = MR.strings.error_no_internet,
                                        isPolling = false
                                    )
                                }
                                sendErrorEffect(MR.strings.error_no_internet)
                            }

                            else -> {
                                // Продолжаем polling при других ошибках
                                Napier.w("PaymentViewModel: Error getting payment status: ${statusResult.message}")
                            }
                        }
                    }
                true
            }

            if (isCompleted == null) {
                // Таймаут
                stopPolling()
                setState {
                    copy(
                        error = MR.strings.error_payment_timeout,
                        isPolling = false
                    )
                }
                sendErrorEffect(MR.strings.error_payment_timeout)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        setState { copy(isPolling = false) }
    }

    private fun retryPayment() {
        setState { copy(error = null) }
        initPayment()
    }

    private fun cancelPayment() {
        viewModelScope.launch {
            setLoading(true)

            val result = cancelPaymentUseCase(state.value.orderId)

            when (result) {
                is Resource.Success -> {
                    setLoading(false)
                    sendEffect(PaymentEffect.PaymentCanceled)
                }

                is Resource.ErrorNoInternet -> {
                    setLoading(false)
                    setState { copy(error = MR.strings.error_no_internet) }
                    sendErrorEffect(MR.strings.error_no_internet)
                }

                else -> {
                    setLoading(false)
                    setState { copy(error = MR.strings.error_payment_cancel_failed) }
                    sendErrorEffect(MR.strings.error_payment_cancel_failed)
                }
            }
        }
    }

    private fun dismissError() {
        setState { copy(error = null) }
    }

    private fun sendErrorEffect(message: StringResource) {
        sendEffect(PaymentEffect.PaymentError(message))
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}


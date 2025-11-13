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
) : BaseViewModel<PaymentEvent, PaymentEffect, PaymentState>() {

    private var pollingJob: Job? = null
    private val maxPollingDuration = 60.seconds

    override fun setInitialState() = PaymentState()


    override fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.InitPayment -> initPayment()
            is PaymentEvent.RetryPayment -> retryPayment()
            is PaymentEvent.CancelPayment -> cancelPayment()
            is PaymentEvent.DismissError -> dismissError()
            is PaymentEvent.SetInitData -> setInitData(event.orderId, event.amount, event.userPhone)
        }
    }

    private fun setInitData(orderId: String, amount: Double, userPhone: String) {
        setState { copy(orderId = orderId, amount = amount, userPhone = userPhone) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun initPayment() {
        viewModelScope.launch {
            Napier.d("PaymentFlow: [ViewModel] initPayment started - orderId=${state.value.orderId}, amount=${state.value.amount}")
            setLoading(true)
            setState { copy(error = null) }

            // 1. Инициализация SDK и получение payment_token
            Napier.d("PaymentFlow: [ViewModel] Calling yooKassaService.initializePayment...")
            val sdkResult = yooKassaService.initializePayment(
                amount = state.value.amount,
                orderId = state.value.orderId,
                userPhone = state.value.userPhone
            )

            Napier.d(
                "PaymentFlow: [ViewModel] SDK result - success=${sdkResult.success}, paymentToken=${
                    sdkResult.paymentToken?.take(
                        20
                    )
                }..., error=${sdkResult.error}"
            )

            if (!sdkResult.success || sdkResult.paymentToken == null) {
                Napier.e("PaymentFlow: [ViewModel] SDK initialization failed - success=${sdkResult.success}, error=${sdkResult.error}")
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
            Napier.d("PaymentFlow: [ViewModel] Creating payment on server - orderId=${state.value.orderId}, amount=${state.value.amount}")
            val description = "Заказ №${state.value.orderId}"
            val createResult = createPaymentUseCase(
                paymentToken = paymentToken,
                orderId = state.value.orderId,
                amount = state.value.amount,
                currency = "RUB",
                description = description
            )

            Napier.d("PaymentFlow: [ViewModel] Payment creation result - success=${createResult is Resource.Success}, paymentId=${(createResult as? Resource.Success)?.data?.paymentId}")

            when (createResult) {
                is Resource.Success -> {
                    val paymentInfo = createResult.data
                    if (paymentInfo == null) {
                        Napier.e("PaymentFlow: [ViewModel] Payment creation returned null data")
                        setLoading(false)
                        setState { copy(error = MR.strings.error_payment_creation_failed) }
                        sendErrorEffect(MR.strings.error_payment_creation_failed)
                        return@launch
                    }

                    val confirmationUrl = paymentInfo.confirmationUrl
                    Napier.d(
                        "PaymentFlow: [ViewModel] Payment created - paymentId=${paymentInfo.paymentId}, confirmationUrl=${
                            confirmationUrl?.take(
                                50
                            )
                        }..., status=${paymentInfo.status}"
                    )

                    // 3. Если есть confirmation_url, открываем форму оплаты
                    if (confirmationUrl != null) {
                        Napier.d("PaymentFlow: [ViewModel] Opening payment form with confirmationUrl...")
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
                        Napier.d("PaymentFlow: [ViewModel] No confirmationUrl, starting polling...")
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
        Napier.d(
            "PaymentFlow: [ViewModel] openPaymentForm - confirmationUrl=${
                confirmationUrl.take(
                    50
                )
            }..."
        )
        val result = yooKassaService.openPaymentUrl(confirmationUrl)
        Napier.d("PaymentFlow: [ViewModel] openPaymentForm result - success=${result.success}, error=${result.error}")

        // После закрытия формы начинаем polling
        setState { copy(isPaymentProcessing = false) }

        if (result.success) {
            Napier.d("PaymentFlow: [ViewModel] Payment form closed successfully, starting polling...")
            startPolling()
        } else {
            Napier.e("PaymentFlow: [ViewModel] Payment form failed - ${result.error}")
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

        Napier.d("PaymentFlow: [ViewModel] startPolling - orderId=${state.value.orderId}")
        setState { copy(isPolling = true) }

        pollingJob = viewModelScope.launch {
            val isCompleted = withTimeoutOrNull(maxPollingDuration) {
                tickerFlow(period = 3.seconds)
                    .collect { _ ->
                        Napier.d("PaymentFlow: [ViewModel] Polling payment status - orderId=${state.value.orderId}")
                        val statusResult = getPaymentStatusUseCase(state.value.orderId)

                        when (statusResult) {
                            is Resource.Success -> {
                                val paymentInfo = statusResult.data
                                val status = paymentInfo?.status
                                Napier.d("PaymentFlow: [ViewModel] Payment status - status=$status, paid=${paymentInfo?.paid}")
                                setState { copy(paymentStatus = status) }

                                when (status) {
                                    PaymentStatus.SUCCEEDED -> {
                                        Napier.d("PaymentFlow: [ViewModel] Payment SUCCEEDED!")
                                        stopPolling()
                                        sendEffect(PaymentSuccess(state.value.orderId))
                                    }

                                    PaymentStatus.CANCELED -> {
                                        Napier.d("PaymentFlow: [ViewModel] Payment CANCELED")
                                        stopPolling()
                                        setState { copy(error = MR.strings.error_payment_canceled) }
                                        sendEffect(PaymentEffect.PaymentCanceled)
                                    }

                                    PaymentStatus.PENDING, PaymentStatus.UNKNOWN, null -> {
                                        // Продолжаем polling
                                        Napier.d("PaymentFlow: [ViewModel] Payment status: $status - continuing polling...")
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
                                Napier.w("PaymentFlow: [ViewModel] Error getting payment status: ${statusResult.message}")
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


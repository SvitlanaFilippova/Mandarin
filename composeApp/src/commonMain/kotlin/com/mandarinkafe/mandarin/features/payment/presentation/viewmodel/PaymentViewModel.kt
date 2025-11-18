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
    private val maxPollingDuration =
        300.seconds // 5 минут - увеличиваем для "умного платежа" на iOS

    override fun setInitialState() = PaymentState()


    override fun onEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.InitPayment -> initPayment()
            is PaymentEvent.RetryPayment -> retryPayment()
            is PaymentEvent.CancelPayment -> cancelPayment()
            is PaymentEvent.DismissError -> dismissError()
            is PaymentEvent.SetInitData -> setInitData(
                event.orderId,
                event.orderNumber,
                event.amount,
                event.userPhone
            )
        }
    }

    private fun setInitData(
        orderId: String,
        orderNumber: String?,
        amount: Double,
        userPhone: String,
    ) {
        setState {
            copy(
                orderId = orderId,
                orderNumber = orderNumber,
                amount = amount,
                userPhone = userPhone
            )
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
            val subtitle = if (state.value.orderNumber != null) {
                "Заказ №${state.value.orderNumber}, ID ${state.value.orderId}"
            } else {
                "Заказ ID ${state.value.orderId}"
            }

            val sdkResult = yooKassaService.initializePayment(
                amount = state.value.amount,
                subtitle = subtitle,
                userPhone = state.value.userPhone
            )

            if (!sdkResult.success) {
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

            // Для iOS "умного платежа" paymentToken может быть null
            // Сервер создаст платеж напрямую через API YooKassa
            val paymentToken = sdkResult.paymentToken

            // 2. Создание платежа на сервере
            val description = if (state.value.orderNumber != null) {
                "Заказ №${state.value.orderNumber}, ID ${state.value.orderId}"
            } else {
                "Заказ ID ${state.value.orderId}"
            }

            // Для iOS paymentToken может быть null - сервер создаст платеж без токена
            // Для iOS также передаем return_url для возврата в приложение после оплаты
            val returnUrl = if (paymentToken == null) {
                // Для iOS "умного платежа" используем URL scheme для возврата в приложение
                "mandarin://payment/return?order_id=${state.value.orderId}"
            } else {
                null // Для Android return_url не нужен (SDK обрабатывает возврат)
            }

            val createResult = if (paymentToken != null) {
                createPaymentUseCase(
                    paymentToken = paymentToken,
                    orderId = state.value.orderId,
                    amount = state.value.amount,
                    currency = "RUB",
                    description = description,
                    returnUrl = returnUrl
                )
            } else {
                // Для iOS "умного платежа" - создаем платеж без payment_token
                createPaymentUseCase(
                    paymentToken = "", // Для iOS сервер создаст платеж без токена
                    orderId = state.value.orderId,
                    amount = state.value.amount,
                    currency = "RUB",
                    description = description,
                    returnUrl = returnUrl
                )
            }

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
                                val status = paymentInfo?.status
                                setState { copy(paymentStatus = status) }

                                when (status) {
                                    PaymentStatus.SUCCEEDED -> {
                                        stopPolling()
                                        sendEffect(
                                            PaymentSuccess(
                                                state.value.orderId,
                                                state.value.amount
                                            )
                                        )
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

    private companion object
}


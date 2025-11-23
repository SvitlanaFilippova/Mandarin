package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.AddPaymentToOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangePaymentMethodUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ForceRefreshOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.PaymentTimerInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.RepeatOrderInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.PaymentTimerInteractorImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.isOnlinePayment
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect.ShowError
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEffect
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEvent
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentViewModel
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForSdk
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.tickerFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class OrderInfoViewModel(
    private val getOrderStatus: GetOrderStatusUseCase,
    private val forceRefreshOrderStatus: ForceRefreshOrderStatusUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val repeatOrderInteractor: RepeatOrderInteractor,
    private val ordersHistoryInteractor: OrdersHistoryInteractor,
    private val paymentViewModel: PaymentViewModel,
    private val addPaymentToOrderUseCase: AddPaymentToOrderUseCase,
    private val changePaymentMethodUseCase: ChangePaymentMethodUseCase,
    private val getPaymentTypesUseCase: GetPaymentTypesUseCase,
) : BaseViewModel<OrderInfoEvent, OrderInfoEffect, OrderInfoState>() {
    override fun setInitialState() = OrderInfoState()

    private var observeJob: Job? = null
    private var paymentStateObserverJob: Job? = null
    private var paymentEffectObserverJob: Job? = null

    private val paymentTimerInteractor: PaymentTimerInteractor = PaymentTimerInteractorImpl(
        cancelOrderUseCase = cancelOrderUseCase,
        getOrderStatusUseCase = getOrderStatus,
        coroutineScope = viewModelScope
    )

    init {
        observePaymentState()
        observePaymentEffects()
    }

    override fun onEvent(event: OrderInfoEvent) {
        when (event) {
            is OrderInfoEvent.SetInitData -> setInitData(event.id, event.paymentMethodCode)
            is OrderInfoEvent.StopObservingStatus -> stopObservingOrderInfo()
            is OrderInfoEvent.CancelOrder -> cancel()
            is OrderInfoEvent.RefreshNow -> forceRefresh()
            is OrderInfoEvent.RepeatOrder -> repeatOrder()
            is OrderInfoEvent.StartPayment -> startPayment()
            is OrderInfoEvent.RetryPayment -> retryPayment()
            is OrderInfoEvent.DeleteOrderFromHistory -> deleteOrderFromHistory()
            is OrderInfoEvent.LoadPaymentTypesForChange -> loadPaymentTypesForChange()
            is OrderInfoEvent.ChangePaymentMethod -> changePaymentMethod(event.paymentMethodCode)
        }
    }

    private fun observePaymentState() {
        paymentStateObserverJob = viewModelScope.launch {
            paymentViewModel.state.collectLatest { paymentState ->
                setState {
                    copy(
                        isPaymentLoading = paymentState.isLoading,
                        isPaymentProcessing = paymentState.isPaymentProcessing,
                        isPaymentPolling = paymentState.isPolling,
                        paymentStatus = paymentState.paymentStatus,
                        paymentError = paymentState.error
                    )
                }
            }
        }
    }

    private fun observePaymentEffects() {
        paymentEffectObserverJob = viewModelScope.launch {
            paymentViewModel.effect.collectLatest { effect ->
                when (effect) {
                    is PaymentEffect.PaymentSuccess -> {
                        // Останавливаем таймер при успешной оплате и сбрасываем флаг автоматической отмены
                        stopPaymentTimer()
                        setState { copy(isAutoCanceling = false) }
                        // Отправляем информацию об оплате в iiko с повторными попытками
                        val orderId = state.value.orderId
                        if (orderId != null) {
                            sendPaymentToIiko(orderId, effect.amount)
                            // Обновляем статус заказа после успешной оплаты
                            delay(PAYMENT_STATUS_UPDATE_DELAY_MS) // Небольшая задержка для обновления на сервере
                            forceRefresh(orderId)
                        }
                    }

                    is PaymentEffect.PaymentError -> {
                        // Сохраняем StringResource в state, конвертация будет в UI
                        setState { copy(paymentError = effect.message) }
                    }

                    is PaymentEffect.PaymentCanceled -> {
                        // Пользователь отменил оплату - просто обновляем состояние
                        setState { copy(paymentError = null) }
                    }

                    is PaymentEffect.ShowCancelDialog -> {
                        // Можно показать диалог отмены, если нужно
                    }

                    is PaymentEffect.PaymentStatusChecked -> {
                        val paymentInfo = effect.paymentInfo
                        setState {
                            copy(
                                paymentStatus = paymentInfo?.status,
                                isPaymentPaid = paymentInfo?.paid
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sendPaymentToIiko(orderId: String, amount: Double) {
        viewModelScope.launch {
            var attempt = 0
            val maxAttempts = PAYMENT_SEND_RETRY_MAX_ATTEMPTS
            var delayMs = PAYMENT_STATUS_UPDATE_DELAY_MS // Начинаем с 1 секунды

            while (attempt < maxAttempts) {
                attempt++
                val result = addPaymentToOrderUseCase(orderId, amount)
                when (result) {
                    is Resource.Success -> {
                        return@launch
                    }

                    is Resource.ErrorNoInternet -> {
                        if (attempt < maxAttempts) {
                            delay(delayMs)
                            delayMs *= 2 // Экспоненциальная задержка: 1s, 2s, 4s
                        }
                    }

                    else -> {
                        if (attempt < maxAttempts) {
                            delay(delayMs)
                            delayMs *= 2
                        }
                    }
                }
            }
        }
    }

    private fun startPayment() {
        val order = state.value.incomingOrder
        val orderId = state.value.orderId

        if (orderId == null || order == null) {
            showError("Не удалось запустить оплату: отсутствует информация о заказе")
            return
        }

        val amount = order.sum ?: 0.0
        val userPhone = order.phone?.formatPhoneNumberForSdk() ?: ""

        if (userPhone.isEmpty()) {
            setState { copy(paymentError = MR.strings.error_payment_init_failed) }
            showError("Не удалось запустить оплату: не указан телефон")
            return
        }

        viewModelScope.launch {
            paymentViewModel.onEvent(
                PaymentEvent.SetInitData(
                    orderId = orderId,
                    orderNumber = order.number,
                    amount = amount,
                    userPhone = userPhone
                )
            )
            paymentViewModel.onEvent(PaymentEvent.InitPayment)
        }
    }

    private fun retryPayment() {
        viewModelScope.launch {
            paymentViewModel.onEvent(PaymentEvent.RetryPayment)
        }
    }

    private fun setInitData(id: String, paymentMethodCode: String? = null) {
        setState { copy(orderId = id, paymentMethodCodeFromNav = paymentMethodCode) }
        observeOrderStatus(id)
    }

    private fun forceRefresh(id: String? = null) {
        viewModelScope.launch {
            val orderId = id ?: state.value.orderId
            if (orderId == null) {
                return@launch
            }
            setLoading()
            val result = forceRefreshOrderStatus(orderId)
            proceedOrderStatusResult(result)
        }
    }

    private fun repeatOrder() {
        viewModelScope.launch {
            setOrderRepeatingInProgress(true)
            val incomingOrder = state.value.incomingOrder
            incomingOrder?.let {
                val result = repeatOrderInteractor.repeatOrder(it.items)
                setOrderRepeatingInProgress(false)
                sendEffect(OrderInfoEffect.RepeatOrder(result.hasInvalidItems))
            }
        }
    }

    private fun setOrderRepeatingInProgress(isActive: Boolean) {
        setState { copy(orderRepeatingInProgress = isActive) }
    }

    private fun cancel() {
        viewModelScope.launch {
            val id = state.value.incomingOrder?.id
            id?.let {
                setLoading()
                val cancelResult = cancelOrderUseCase.invoke(it)
                if (cancelResult is Resource.Success) {
                    delay(ORDER_STATUS_UPD_DELAY_AFTER_CANCEL)
                    val result = getOrderStatus(it)
                    proceedOrderStatusResult(result)
                } else {
                    showError()
                }
            }
        }
    }

    private fun proceedOrderStatusResult(result: Resource<IncomingOrder>) {
        when (result) {
            is Resource.Loading -> setLoading()

            is Resource.Success -> {
                val order = result.data
                if (order == null) {
                    showError()
                    return
                }
                setStatus(order)
                if (order.isClosed) {
                    stopObservingOrderInfo()
                    stopPaymentTimer()
                }
            }

            is Resource.ErrorNoInternet -> showError("Нет подключения к интернету")

            is Resource.Idle -> {
                // Запрос был проигнорирован из-за TTL (слишком частый запрос)
                // Просто убираем loading, не показываем ошибку
                setLoading(false)
            }

            else -> showError(result.message ?: DEFAULT_ERROR_MESSAGE)
        }
    }

    private fun observeOrderStatus(orderId: String) {
        stopObservingOrderInfo()
        observeJob = viewModelScope.launch {
            tickerFlow(period = ORDER_STATUS_UPD_DELAY.seconds)
                .onStart { emit(Unit) }
                .map {
                    getOrderStatus(orderId)
                }
                .collect { result ->
                    proceedOrderStatusResult(result)
                    // Параллельно проверяем статус оплаты, если заказ с онлайн-оплатой
                    result.data?.let { order ->
                        // Проверяем статус оплаты даже для отменённых заказов
                        if (order.isOnlinePayment(state.value.paymentMethodCodeFromNav)) {
                            checkPaymentStatus(orderId)
                        }
                    }
                }
        }
    }

    private fun stopObservingOrderInfo() {
        observeJob?.cancel()
        observeJob = null
        // Останавливаем таймер, когда прекращаем наблюдение за заказом
        stopPaymentTimer()
        setState { copy(isAutoCanceling = false) }
    }

    private fun showError(msg: String? = DEFAULT_ERROR_MESSAGE) {
        msg?.let {
            sendEffect(ShowError(msg))
        }
        setLoading(false)
    }

    private fun setStatus(status: IncomingOrder?) {
        val previousOrder = state.value.incomingOrder
        val previousPaymentDeadline = previousOrder?.paymentDeadline

        setState { copy(isLoading = false, incomingOrder = status) }

        // Если заказ с онлайн-оплатой, проверяем статус платежа
        // Проверяем даже для отменённых заказов, чтобы знать, была ли оплата успешной
        status?.let { order ->
            if (order.isOnlinePayment(state.value.paymentMethodCodeFromNav)) {
                // Останавливаем таймер, если заказ закрыт или оплачен
                if (order.isClosed || state.value.paymentStatus == PaymentStatus.SUCCEEDED || state.value.isPaymentPaid == true) {
                    stopPaymentTimer()
                    setState { copy(isAutoCanceling = false) }
                    checkPaymentStatus(order.id)
                    return@let
                }

                checkPaymentStatus(order.id)
                // Запускаем таймер оплаты, если есть дедлайн
                // Также запускаем, если paymentDeadline появился (был null, стал не null) или изменился
                val shouldStartTimer = order.paymentDeadline != null &&
                        (previousPaymentDeadline == null ||
                                previousPaymentDeadline != order.paymentDeadline)

                if (shouldStartTimer) {
                    startPaymentTimer(order.paymentDeadline)
                }
                // Если paymentDeadline null, не запускаем таймер, но и не останавливаем существующий
                // Таймер запустится автоматически при следующем обновлении, когда paymentDeadline появится
            } else {
                // Если не онлайн-оплата, останавливаем таймер
                stopPaymentTimer()
            }
        } ?: run {
            // Если заказ null, останавливаем таймер
            stopPaymentTimer()
        }
    }

    private fun checkPaymentStatus(orderId: String) {
        viewModelScope.launch {
            paymentViewModel.onEvent(PaymentEvent.CheckPaymentStatus(orderId))
        }
    }

    private fun deleteOrderFromHistory() {
        val orderId = state.value.orderId
        if (orderId == null) {
            showError("Не указан ID заказа")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            try {
                ordersHistoryInteractor.removeOrderById(orderId)
                setLoading(false)
                sendEffect(OrderInfoEffect.NavigateBack)
            } catch (e: Exception) {
                Napier.e("OrderInfoViewModel, deleteOrderFromHistory error: $e")
                setLoading(false)
                showError("Не удалось удалить заказ из истории")
            }
        }
    }

    private fun loadPaymentTypesForChange() {
        viewModelScope.launch {
            // Загружаем доступные способы оплаты
            val paymentTypesResult = getPaymentTypesUseCase()
            when (paymentTypesResult) {
                is Resource.Success -> {
                    val paymentTypes = paymentTypesResult.data ?: emptyList()
                    // Фильтруем только CASH, BANK, ONLINE (как в OrderViewModel)
                    val filteredTypes = paymentTypes.filter {
                        it.code.equals(
                            Constants.PAYMENT_CASH_CODE,
                            ignoreCase = true
                        ) ||
                                it.code.equals(
                                    Constants.PAYMENT_BANK_CODE,
                                    ignoreCase = true
                                ) ||
                                it.code.equals(
                                    PAYMENT_ONLINE_CODE,
                                    ignoreCase = true
                                )
                    }
                    setState {
                        copy(availablePaymentTypes = filteredTypes)
                    }
                }

                else -> {
                    showError("Не удалось загрузить способы оплаты")
                }
            }
        }
    }

    private fun changePaymentMethod(paymentMethodCode: String) {
        val orderId = state.value.orderId
        if (orderId == null) {
            showError("Не указан ID заказа")
            return
        }

        viewModelScope.launch {
            setState { copy(isChangingPaymentMethod = true) }

            val result = changePaymentMethodUseCase(orderId, paymentMethodCode)

            when (result) {
                is Resource.Success -> {
                    // Обновляем заказ с сервера
                    val orderResult = getOrderStatus(orderId)
                    // Обрабатываем результат обновления заказа и запускаем оплату, если нужно
                    proceedOrderStatusResultAfterPaymentChange(orderResult, paymentMethodCode)
                }

                is Resource.ErrorNoInternet -> {
                    setState { copy(isChangingPaymentMethod = false) }
                    showError("Нет подключения к интернету")
                }

                else -> {
                    setState { copy(isChangingPaymentMethod = false) }
                    // Сервер вернул ошибку - не удалось изменить способ оплаты (в т.ч. из-за ошибки обновления комментария в iiko)
                    showError("Не удалось изменить способ оплаты")
                }
            }
        }
    }

    private fun proceedOrderStatusResultAfterPaymentChange(
        result: Resource<IncomingOrder>,
        newPaymentMethodCode: String,
    ) {
        when (result) {
            is Resource.Success -> {
                val order = result.data
                if (order == null) {
                    setState { copy(isChangingPaymentMethod = false) }
                    showError("Не удалось получить обновленную информацию о заказе")
                    return
                }

                // Обновляем состояние заказа
                setStatus(order)
                // Обновляем paymentMethodCodeFromNav для немедленного отображения
                setState {
                    copy(
                        isChangingPaymentMethod = false,
                        paymentMethodCodeFromNav = newPaymentMethodCode
                    )
                }

                // Если выбран способ оплаты ONLINE, запускаем процесс оплаты
                if (newPaymentMethodCode.equals(PAYMENT_ONLINE_CODE, ignoreCase = true)) {
                    // Запускаем оплату (startPayment сам проверит наличие телефона и покажет ошибку при необходимости)
                    startPayment()
                }
            }

            is Resource.ErrorNoInternet -> {
                setState { copy(isChangingPaymentMethod = false) }
                showError("Нет подключения к интернету")
            }

            is Resource.Idle -> {
                // Запрос был проигнорирован из-за TTL (слишком частый запрос)
                // Обновляем paymentMethodCodeFromNav, но не запускаем оплату
                setState {
                    copy(
                        isChangingPaymentMethod = false,
                        paymentMethodCodeFromNav = newPaymentMethodCode
                    )
                }
            }

            else -> {
                setState { copy(isChangingPaymentMethod = false) }
                showError(result.message ?: "Не удалось получить обновленную информацию о заказе")
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun startPaymentTimer(paymentDeadline: Long?) {
        if (paymentDeadline == null) {
            setState { copy(paymentTimeRemainingSeconds = null) }
            return
        }

        val orderId = state.value.orderId ?: return

        paymentTimerInteractor.startTimer(
            paymentDeadline = paymentDeadline,
            orderId = orderId,
            onTimeUpdate = { remainingSeconds ->
                setState { copy(paymentTimeRemainingSeconds = remainingSeconds) }
            },
            shouldStopTimer = {
                val currentState = state.value
                currentState.paymentStatus == PaymentStatus.SUCCEEDED ||
                        currentState.isPaymentPaid == true ||
                        currentState.incomingOrder?.isClosed == true ||
                        !currentState.incomingOrder.isOnlinePayment(currentState.paymentMethodCodeFromNav)
            },
            onTimeout = {
                autoCancelOrderOnTimeout()
            }
        )
    }

    private fun stopPaymentTimer() {
        paymentTimerInteractor.stopTimer()
        setState { copy(paymentTimeRemainingSeconds = null, isAutoCanceling = false) }
    }

    private suspend fun autoCancelOrderOnTimeout() {
        val orderId = state.value.orderId
        if (orderId == null || state.value.isAutoCanceling) {
            return // Уже идет отмена или нет ID заказа
        }
        paymentTimerInteractor.autoCancelOnTimeout(
            orderId = orderId,
            canCancel = {
                paymentTimerInteractor.canCancelOrder(
                    order = state.value.incomingOrder,
                    paymentStatus = state.value.paymentStatus,
                    isPaymentPaid = state.value.isPaymentPaid,
                    paymentMethodCodeFromNav = state.value.paymentMethodCodeFromNav
                )
            },
            onCancelStarted = {
                setState { copy(isAutoCanceling = true) }
            },
            onCancelCompleted = { updatedOrderId ->
                val result = getOrderStatus(updatedOrderId)
                proceedOrderStatusResult(result)
            },
            onCancelFailed = {
                setState { copy(isAutoCanceling = false) }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        paymentStateObserverJob?.cancel()
        paymentEffectObserverJob?.cancel()
        paymentTimerInteractor.stopTimer()
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 60
        const val ORDER_STATUS_UPD_DELAY_AFTER_CANCEL = 500L
        const val PAYMENT_SEND_RETRY_MAX_ATTEMPTS = 3
        const val PAYMENT_STATUS_UPDATE_DELAY_MS = 1000L
        private const val DEFAULT_ERROR_MESSAGE = "Что-то пошло не так"
    }
}


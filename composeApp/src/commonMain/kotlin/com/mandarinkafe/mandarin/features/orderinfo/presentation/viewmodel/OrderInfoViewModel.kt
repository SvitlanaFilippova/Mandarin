package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.cart.domain.api.CartInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ForceRefreshOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.RepeatOrderInteractor
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect.ShowError
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.payment.domain.api.GetPaymentStatusUseCase
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEffect
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEvent
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentViewModel
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForSdk
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.tickerFlow
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
    private val cartInteractor: CartInteractor,
    private val getPaymentStatus: GetPaymentStatusUseCase,
    private val ordersHistoryInteractor: OrdersHistoryInteractor,
    private val paymentViewModel: PaymentViewModel,
) : BaseViewModel<OrderInfoEvent, OrderInfoEffect, OrderInfoState>() {
    override fun setInitialState() = OrderInfoState()

    private var observeJob: Job? = null
    private var paymentStateObserverJob: Job? = null
    private var paymentEffectObserverJob: Job? = null

    init {
        observePaymentState()
        observePaymentEffects()
    }

    override fun onEvent(event: OrderInfoEvent) {
        when (event) {
            is OrderInfoEvent.SetInitId -> setInitData(event.id)
            is OrderInfoEvent.StopObservingStatus -> stopObservingOrderInfo()
            is OrderInfoEvent.CancelOrder -> cancel()
            is OrderInfoEvent.RefreshNow -> forceRefresh()
            is OrderInfoEvent.RepeatOrder -> repeatOrder()
            is OrderInfoEvent.StartPayment -> startPayment()
            is OrderInfoEvent.RetryPayment -> retryPayment()
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
                        // Обновляем статус заказа после успешной оплаты
                        val orderId = state.value.orderId
                        if (orderId != null) {
                            delay(1000) // Небольшая задержка для обновления на сервере
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
                }
            }
        }
    }

    private fun startPayment() {
        val order = state.value.incomingOrder
        val savedOrder = state.value.savedOrder
        val orderId = state.value.orderId
        
        if (orderId == null || order == null) {
            return
        }

        val amount = order.sum ?: 0.0
        val userPhone = order.phone?.formatPhoneNumberForSdk() 
            ?: savedOrder?.let { 
                // Если телефона нет в заказе, можно попробовать получить из сохраненных данных
                // Но обычно телефон должен быть в заказе
                ""
            } ?: ""

        if (userPhone.isEmpty()) {
            setState { copy(paymentError = MR.strings.error_payment_init_failed) }
            return
        }

        viewModelScope.launch {
            paymentViewModel.onEvent(
                PaymentEvent.SetInitData(
                    orderId = orderId,
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

    private fun setInitData(id: String) {
        setState { copy(orderId = id) }
        loadSavedOrder(id)
        observeOrderStatus(id)
    }

    private fun loadSavedOrder(orderId: String) {
        viewModelScope.launch {
            val savedOrder = ordersHistoryInteractor.getOrderById(orderId)
            setState { copy(savedOrder = savedOrder) }
            
            // Если заказ с онлайн-оплатой и не закрыт, проверяем статус платежа
            savedOrder?.let { order ->
                if (order.paymentMethodCode?.equals(PAYMENT_ONLINE_CODE, ignoreCase = true) == true) {
                    val incomingOrder = state.value.incomingOrder
                    if (incomingOrder != null && !incomingOrder.isClosed) {
                        checkPaymentStatus(orderId)
                    }
                }
            }
        }
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
                val result = repeatOrderInteractor.mapToCartItems(it.items)
                result.cartItems.forEach { cartInteractor.addItem(it) }
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
                }
            }

            is Resource.ErrorNoInternet -> showError("Нет подключения к интернету")

            is Resource.Idle -> {
                // Запрос был проигнорирован из-за TTL (слишком частый запрос)
                // Просто убираем loading, не показываем ошибку
                setLoading(false)
            }

            else -> showError(result.message ?: "Что-то пошло не так")
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
                        val isOnlinePayment = state.value.isOnlinePayment
                        if (isOnlinePayment && !order.isClosed) {
                            checkPaymentStatus(orderId)
                        }
                    }
                }
        }
    }

    private fun stopObservingOrderInfo() {
        observeJob?.cancel()
        observeJob = null
    }

    private fun showError(msg: String? = "Что-то пошло не так") {
        msg?.let {
            sendEffect(ShowError(msg))
        }
        setLoading(false)
    }

    private fun setStatus(status: IncomingOrder?) {
        setState { copy(isLoading = false, incomingOrder = status) }
        
        // Если заказ с онлайн-оплатой, проверяем статус платежа
        status?.let { order ->
            val isOnlinePayment = state.value.isOnlinePayment
            if (isOnlinePayment && !order.isClosed) {
                checkPaymentStatus(order.id)
            }
        }
    }
    
    private fun checkPaymentStatus(orderId: String) {
        viewModelScope.launch {
            val result = getPaymentStatus(orderId)
            when (result) {
                is Resource.Success -> {
                    val paymentInfo = result.data
                    setState {
                        copy(
                            paymentStatus = paymentInfo?.status,
                            isPaymentPaid = paymentInfo?.paid
                        )
                    }
                }
                else -> {
                    // Игнорируем ошибки получения статуса платежа (платеж может не существовать)
                    // Не обновляем состояние
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    override fun onCleared() {
        super.onCleared()
        paymentStateObserverJob?.cancel()
        paymentEffectObserverJob?.cancel()
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 60
        const val ORDER_STATUS_UPD_DELAY_AFTER_CANCEL = 500L
    }
}


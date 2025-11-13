package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import androidx.lifecycle.viewModelScope
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
import com.mandarinkafe.mandarin.features.payment.domain.api.GetPaymentStatusUseCase
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.tickerFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
) : BaseViewModel<OrderInfoEvent, OrderInfoEffect, OrderInfoState>() {
    override fun setInitialState() = OrderInfoState()

    private var observeJob: Job? = null

    override fun onEvent(event: OrderInfoEvent) {
        when (event) {
            is OrderInfoEvent.SetInitId -> setInitData(event.id)
            is OrderInfoEvent.StopObservingStatus -> stopObservingOrderInfo()
            is OrderInfoEvent.CancelOrder -> cancel()
            is OrderInfoEvent.RefreshNow -> forceRefresh()
            is OrderInfoEvent.RepeatOrder -> repeatOrder()
        }
    }

    private fun setInitData(id: String) {
        setState { copy(orderId = id) }
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
                        if (order.paymentName?.equals(PAYMENT_ONLINE_CODE, ignoreCase = true) == true && !order.isClosed) {
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
            if (order.paymentName?.equals(PAYMENT_ONLINE_CODE, ignoreCase = true) == true && !order.isClosed) {
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

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 60
        const val ORDER_STATUS_UPD_DELAY_AFTER_CANCEL = 500L
    }
}


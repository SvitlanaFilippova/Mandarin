package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetCurrentStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect.ShowError
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderInfoViewModel @Inject constructor(
    private val observeOrderStatus: ObserveOrderStatusUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val getCurrentStatusUseCase: GetCurrentStatusUseCase
) : BaseViewModel<OrderInfoEvent, OrderInfoEffect, OrderInfoState>() {
    override fun setInitialState() = OrderInfoState()

    private val logTag = "OrderInfo DEBUG - VM"

    override fun onEvent(event: OrderInfoEvent) {
        when (event) {
            is OrderInfoEvent.SetInitId -> setInitData(event.id)
            is OrderInfoEvent.StopObservingStatus -> stopObservingOrderStatus()
            is OrderInfoEvent.CancelOrder -> cancel()
            is OrderInfoEvent.RefreshNow -> forceRefresh()
            is OrderInfoEvent.RepeatOrder -> repeatOrder()
        }
    }

    private fun setInitData(id: String) {
        setState { copy(orderId = id) }
        forceRefresh(id)
    }

    private fun forceRefresh(id: String? = null) {
        viewModelScope.launch {
            val orderId = id ?: state.value.orderId
            if (orderId == null) {
                return@launch
            }
            setLoading()
            val result = getCurrentStatusUseCase(orderId)
            proceedOrderStatusResult(result)
        }
    }

    private fun repeatOrder() {
        TODO("Not yet implemented")
    }

    private fun cancel() {
        viewModelScope.launch {
            val id = state.value.incomingOrder?.id
            id?.let {
                cancelOrderUseCase.invoke(it)
                setLoading()
                delay(ORDER_STATUS_UPD_DELAY_AFTER_CANCEL)
                val result = getCurrentStatusUseCase(it)
                proceedOrderStatusResult(result)
            }
        }
    }

    private var observeStatusJob: Job? = null

    private fun proceedOrderStatusResult(result: Resource<IncomingOrder>) {
        when (result) {
            is Resource.Loading -> setLoading()

            is Resource.Success -> {
                val order = result.data
                if (order == null) {
                    showError("Что-то пошло не так")
                    return
                }
                setStatus(order)
                if (!order.isClosed) {
                    startObservingOrderStatus()
                } else {
                    stopObservingOrderStatus()
                }
            }

            is Resource.ErrorNoInternet -> showError("Нет подключения к интернету")

            else -> showError(result.message ?: "Что-то пошло не так")
        }
    }

    private fun startObservingOrderStatus(orderId: String? = null) {
        val id = orderId ?: state.value.orderId ?: return
        observeStatusJob?.cancel()
        observeStatusJob = viewModelScope.launch {
            observeOrderStatus(id, ORDER_STATUS_UPD_DELAY)
                .collect { result ->
                    Log.d(
                        logTag,
                        "startObservingOrderStatus, collected response, status: ${result.data?.status} "
                    )
                    proceedOrderStatusResult(result)
                }
        }
    }

    private fun stopObservingOrderStatus() {
        observeStatusJob?.cancel()
    }

    private fun showError(msg: String?) {
        msg?.let {
            sendEffect(ShowError(msg))
        }
    }

    private fun setStatus(status: IncomingOrder?) {
        setState { copy(isLoading = false, incomingOrder = status) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 20000L
        const val ORDER_STATUS_UPD_DELAY_AFTER_CANCEL = 500L
    }
}
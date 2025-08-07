package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEffect.ShowError
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderConfirmationViewModel @Inject constructor(private val observeOrderStatus: ObserveOrderStatusUseCase) :
    BaseViewModel<OrderConfirmationEvent, OrderConfirmationEffect, OrderConfirmationState>() {
    override fun setInitialState() = OrderConfirmationState()

    override fun onEvent(event: OrderConfirmationEvent) {
        when (event) {
            is OrderConfirmationEvent.SetInitId -> startObservingOrderStatus(event.id)
            OrderConfirmationEvent.StopObservingStatus -> stopObservingOrderStatus()
        }
    }

    private var observeStatusJob: Job? = null

    private fun startObservingOrderStatus(orderId: String) {
        observeStatusJob?.cancel()
        observeStatusJob = viewModelScope.launch {
            observeOrderStatus(orderId, ORDER_STATUS_UPD_DELAY)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> setLoading()
                        is Resource.Success -> setStatus(result.data)
                        is Resource.ErrorNoInternet -> showError("Нет подключения к интернету")
                        else -> showError(result.message ?: "Что-то пошло не так")
                    }
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
        const val ORDER_STATUS_UPD_DELAY = 10000L
    }
}
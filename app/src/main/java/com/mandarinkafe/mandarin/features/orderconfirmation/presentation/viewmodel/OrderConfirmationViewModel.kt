package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEffect
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEffect.ShowError
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEvent
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationState
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
        }
    }

    private var observeStatusJob: Job? = null

    val logTag = "OBSERVE STATUS VM"
    private fun startObservingOrderStatus(orderId: String) {
        observeStatusJob?.cancel() // отмена предыдущего
        observeStatusJob = viewModelScope.launch {
            Log.d(logTag, "started startObservingOrderStatus")
            observeOrderStatus(orderId)
                .collect { statusResponse ->
                    Log.d(logTag, "statusResponse: $statusResponse")
                    when (statusResponse) {
                        is Resource.Loading -> setLoading()
                        is Resource.Success -> setStatus(statusResponse.data)
                        is Resource.ErrorNoInternet -> showError("Нет подключения к интернету")
                        else -> showError(statusResponse.message ?: "Что-то пошло не так")
                    }
                }
        }
    }

    fun stopObservingOrderStatus() {
        observeStatusJob?.cancel()
    }

    private fun showError(msg: String?) {
        msg?.let {
            sendEffect(ShowError(msg))
        }
    }

    private fun setStatus(status: String?) {
        setState { copy(isLoading = false, status = status) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

}
package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryState
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersHistoryViewModel @Inject constructor(private val getHistory: GetOrdersHistoryUseCase) :
    BaseViewModel<OrdersHistoryEvent, OrdersHistoryEffect, OrdersHistoryState>() {
    override fun setInitialState() = OrdersHistoryState()

    init {
        viewModelScope.launch {
            val history = getHistory()
            setState { copy(data = history) }
        }
    }

    override fun onEvent(event: OrdersHistoryEvent) {
        when (event) {
            else -> {}
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}
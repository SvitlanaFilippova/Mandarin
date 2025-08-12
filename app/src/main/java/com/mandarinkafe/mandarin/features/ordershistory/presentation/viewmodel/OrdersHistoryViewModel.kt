package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersStatusesUseCase
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryState
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersHistoryViewModel @Inject constructor(
    private val getHistory: GetOrdersHistoryUseCase,
    private val getOrdersStatuses: GetOrdersStatusesUseCase
) :
    BaseViewModel<OrdersHistoryEvent, OrdersHistoryEffect, OrdersHistoryState>() {
    override fun setInitialState() = OrdersHistoryState()

    init {
        refreshData()
    }

    override fun onEvent(event: OrdersHistoryEvent) {
        when (event) {
            OrdersHistoryEvent.ForceRefresh -> refreshData()
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            val history = getHistory()
            setState { copy(data = history) }

            val statusesResponse = getOrdersStatuses.invoke(history)
            if (statusesResponse.data != null) {
                setState { copy(data = statusesResponse.data) }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}
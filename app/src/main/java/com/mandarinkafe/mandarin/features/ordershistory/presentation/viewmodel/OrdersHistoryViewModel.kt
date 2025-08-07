package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel

import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryState
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersHistoryViewModel @Inject constructor() :
    BaseViewModel<OrdersHistoryEvent, OrdersHistoryEffect, OrdersHistoryState>() {
    override fun setInitialState() = OrdersHistoryState()

    override fun onEvent(event: OrdersHistoryEvent) {
        when (event) {
            else -> {}
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}
package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface OrdersHistoryContract {
    sealed interface OrdersHistoryEvent : BaseEvent

    sealed interface OrdersHistoryEffect : BaseEffect

    data class OrdersHistoryState(
        val isLoading: Boolean? = null,
        val data: List<SavedOrder> = emptyList()
    ) : BaseState
}
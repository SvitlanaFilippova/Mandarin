package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateFilterType
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateRange
import com.mandarinkafe.mandarin.util.presentation.BaseEffect
import com.mandarinkafe.mandarin.util.presentation.BaseEvent
import com.mandarinkafe.mandarin.util.presentation.BaseState

sealed interface OrdersHistoryContract {
    sealed interface OrdersHistoryEvent : BaseEvent {
        data object ForceRefresh : OrdersHistoryEvent
        data class RemoveOrderFromHistory(val id: String) : OrdersHistoryEvent
        data class SetChosenOrderTypes(val filter: List<DeliveryType>) : OrdersHistoryEvent
        data class SetChosenDateFilter(val filter: DateFilterType?) : OrdersHistoryEvent
        data class SetChosenDateRange(val range: DateRange) : OrdersHistoryEvent
    }

    sealed interface OrdersHistoryEffect : BaseEffect {
        data class ShowError(val message: String) : OrdersHistoryEffect
    }

    data class OrdersHistoryState(
        val isLoading: Boolean? = null,
        val fullData: List<SavedOrder> = emptyList(),
        val filteredData: List<SavedOrder> = emptyList(),
        val chosenOrderTypes: List<DeliveryType> = emptyList(),
        val chosenDateFilterType: DateFilterType? = null,
        val chosenDateRange: DateRange? = null
    ) : BaseState {
        val anyFiltersAreApplied: Boolean
            get() = chosenOrderTypes.isNotEmpty() || chosenDateFilterType != null
    }
}
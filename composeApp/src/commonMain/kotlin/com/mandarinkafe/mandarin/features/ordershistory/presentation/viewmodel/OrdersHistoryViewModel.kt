package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersStatusesUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateFilterType
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateRange
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect.ShowError
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryState
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.helpers.OrdersFilter
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.tickerFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

class OrdersHistoryViewModel(
    private val historyInteractor: OrdersHistoryInteractor,
    private val getOrdersStatuses: GetOrdersStatusesUseCase
) : BaseViewModel<OrdersHistoryEvent, OrdersHistoryEffect, OrdersHistoryState>() {
    override fun setInitialState() = OrdersHistoryState()

    private var observeJob: Job? = null

    override fun onEvent(event: OrdersHistoryEvent) {
        when (event) {
            is OrdersHistoryEvent.ForceRefresh -> refreshData()

            is OrdersHistoryEvent.SetChosenOrderTypes -> {
                setState { copy(chosenOrderTypes = event.filter) }
                setState { copy(filteredData = applyCurrentFilters(fullData)) }
            }

            is OrdersHistoryEvent.SetChosenDateFilter -> {
                setState { copy(chosenDateFilterType = event.filter) }
                setState { copy(filteredData = applyCurrentFilters(fullData)) }
            }

            is OrdersHistoryEvent.SetChosenDateRange -> {
                setState { copy(chosenDateRange = event.range) }
                setState { copy(filteredData = applyCurrentFilters(fullData)) }
            }

            is OrdersHistoryEvent.RemoveOrderFromHistory -> {
                removeOrderFromHistory(event.id)
            }
        }
    }



    private fun refreshData() {
        viewModelScope.launch {
            setLoading()
            val history = historyInteractor.getHistory()
            setData(history)
            val statusesResponse = getOrdersStatuses(history)
            val data = statusesResponse.data
            if (data != null) {
                setData(data)
                if (data.any { it.isActive }) {
                    observeOrdersStatus(data)
                }
            }
        }
    }

    private fun observeOrdersStatus(data: List<SavedOrder>) {
        stopObservingOrderInfo()
        observeJob = viewModelScope.launch {
            tickerFlow(period = ORDER_STATUS_UPD_DELAY.seconds)
                .map {
                    val activeOrders = data.filter { it.isActive }
                    if (activeOrders.isNotEmpty()) {
                        getOrdersStatuses(activeOrders)
                    } else {
                        stopObservingOrderInfo()
                        null
                    }
                }
                .collect { result ->
                    result?.let { proceedOrderStatusResult(it) }
                }
        }
    }

    private fun proceedOrderStatusResult(result: Resource<List<SavedOrder>>) {
        when (result) {
            is Resource.Loading -> setLoading()

            is Resource.Success -> {
                val updatedOrders = result.data ?: run {
                    showError("Что-то пошло не так при попытке обновить статусы заказов")
                    return
                }

                // Обновляем только активные заказы
                val mergedOrders = state.value.fullData.map { oldOrder ->
                    val newOrder = updatedOrders.find { it.id == oldOrder.id }
                    if (newOrder != null) {
                        oldOrder.copy(status = newOrder.status)
                    } else {
                        oldOrder
                    }
                }

                setData(mergedOrders)

                // Если активных не осталось — останавливаем наблюдение
                if (mergedOrders.none { it.isActive }) {
                    stopObservingOrderInfo()
                }
            }

            is Resource.ErrorNoInternet -> showError("Нет подключения к интернету")
            else -> showError(
                result.message ?: "Что-то пошло не так при попытке обновить статусы заказов"
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private fun stopObservingOrderInfo() {
        observeJob?.cancel()
        observeJob = null
    }

    private fun setData(data: List<SavedOrder>) {
        setState {
            copy(
                isLoading = false,
                fullData = data,
                filteredData = applyCurrentFilters(data)
            )
        }
    }

    private fun applyCurrentFilters(data: List<SavedOrder>): List<SavedOrder> {
        // Фильтруем по типу
        val filteredByType = if (state.value.chosenOrderTypes.isNotEmpty()) {
            data.filter { it.orderType in state.value.chosenOrderTypes }
        } else {
            data
        }

        // Фильтруем по дате
        val filteredByDate = applyDateFilter(
            filteredByType,
            state.value.chosenDateFilterType,
            state.value.chosenDateRange
        )

        return filteredByDate
    }

    @OptIn(kotlin.time.ExperimentalTime::class)
    private fun applyDateFilter(
        orders: List<SavedOrder>,
        filter: DateFilterType?,
        dateRange: DateRange? = null
    ): List<SavedOrder> {
        return when (filter) {
            null -> orders
            DateFilterType.TODAY -> OrdersFilter.today(orders)
            DateFilterType.YESTERDAY -> OrdersFilter.yesterday(orders)
            DateFilterType.LAST_7_DAYS -> OrdersFilter.lastNDays(orders, SEVEN_DAYS_TIME_FILTER)
            DateFilterType.CURRENT_MONTH -> {
                val zone = TimeZone.currentSystemDefault()
                val today = kotlin.time.Clock.System.now().toLocalDateTime(zone).date
                val firstDayOfMonth = LocalDate(today.year, today.month, 1)

               OrdersFilter.betweenDates(
                    orders,
                    firstDayOfMonth,
                    today
                )
            }

            DateFilterType.CUSTOM_RANGE -> {
                if (dateRange != null) {
                    OrdersFilter.betweenDates(orders, dateRange.start, dateRange.end)
                } else {
                    orders
                }
            }
        }
    }

    private fun removeOrderFromHistory(id: String) {
        viewModelScope.launch {
            historyInteractor.removeOrderById(id)
        }
    }

    private fun showError(message: String) {
        sendEffect(ShowError(message))
    }

    private companion object {
        const val SEVEN_DAYS_TIME_FILTER = 7
        const val ORDER_STATUS_UPD_DELAY = 10
    }
}

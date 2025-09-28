package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.helpers

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import java.time.LocalDate
import java.time.ZoneId

object OrdersFilter {

    /**
     * Заказы, созданные сегодня (по локальному времени).
     */
    fun today(orders: List<SavedOrder>): List<SavedOrder> {
        val zoneId = ZoneId.systemDefault()
        val startOfDay = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return orders.filter { it.timestamp >= startOfDay }
    }

    /**
     * Заказы, созданные вчера (по локальному времени).
     */
    fun yesterday(orders: List<SavedOrder>): List<SavedOrder> {
        val zoneId = ZoneId.systemDefault()
        val startOfToday = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val startOfYesterday =
            LocalDate.now(zoneId).minusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return orders.filter { it.timestamp in startOfYesterday until startOfToday }
    }

    /**
     * Заказы за последние [days] дней (включая сегодня).
     */
    fun lastNDays(orders: List<SavedOrder>, days: Long): List<SavedOrder> {
        val zoneId = ZoneId.systemDefault()
        val cutoff = LocalDate.now(zoneId)
            .minusDays(days - 1) // чтобы сегодня входил
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
        return orders.filter { it.timestamp >= cutoff }
    }

    /**
     * Заказы в диапазоне дат [fromDate]..[toDate] включительно.
     * fromDate и toDate — LocalDate в локальном часовом поясе.
     */
    fun betweenDates(
        orders: List<SavedOrder>,
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<SavedOrder> {
        val zoneId = ZoneId.systemDefault()
        val startMillis = fromDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = toDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        return orders.filter { it.timestamp in startMillis..endMillis }
    }
}
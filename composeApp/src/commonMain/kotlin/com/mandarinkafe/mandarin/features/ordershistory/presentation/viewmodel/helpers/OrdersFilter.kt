package com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.helpers

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock


@OptIn(kotlin.time.ExperimentalTime::class)
object OrdersFilter {
    /**
     * Заказы, созданные сегодня (по локальному времени).
     */
    fun today(orders: List<SavedOrder>): List<SavedOrder> {
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        val startOfDay = now.toLocalDateTime(zone).date
            .atStartOfDayIn(zone)
            .toEpochMilliseconds()
        return orders.filter { it.timestamp >= startOfDay }
    }

    /**
     * Заказы, созданные вчера (по локальному времени).
     */
    fun yesterday(orders: List<SavedOrder>): List<SavedOrder> {
        val zone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(zone).date
        val startOfToday = today.atStartOfDayIn(zone).toEpochMilliseconds()
        val startOfYesterday = today.minus(DatePeriod(days = 1)).atStartOfDayIn(zone).toEpochMilliseconds()
        return orders.filter { it.timestamp in startOfYesterday until startOfToday }
    }

    /**
     * Заказы за последние [days] дней (включая сегодня).
     */
    fun lastNDays(orders: List<SavedOrder>, days: Int): List<SavedOrder> {
        val zone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(zone).date
        val cutoff = today
            .minus(DatePeriod(days = days - 1))
            .atStartOfDayIn(zone)
            .toEpochMilliseconds()
        return orders.filter { it.timestamp >= cutoff }
    }

    /**
     * Заказы в диапазоне дат [fromDate]..[toDate] включительно.
     */
    fun betweenDates(
        orders: List<SavedOrder>,
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<SavedOrder> {
        val zone = TimeZone.currentSystemDefault()
        val startMillis = fromDate.atStartOfDayIn(zone).toEpochMilliseconds()
        val endMillis = toDate
            .plus(DatePeriod(days = 1))
            .atStartOfDayIn(zone)
            .toEpochMilliseconds() - 1
        return orders.filter { it.timestamp in startMillis..endMillis }
    }
}

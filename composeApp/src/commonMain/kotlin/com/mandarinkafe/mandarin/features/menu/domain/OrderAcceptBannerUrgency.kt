@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.mandarinkafe.mandarin.features.menu.domain

import com.mandarinkafe.mandarin.features.menu.domain.models.OrderAcceptStatusSnapshot
import com.mandarinkafe.mandarin.util.Constants.ORDER_ACCEPT_ENDING_SOON_THRESHOLD_MS
import com.mandarinkafe.mandarin.util.Constants.ORDER_ACCEPT_LOCAL_TIME_ZONE_ID
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Баннер на главной: «осталось меньше порога до orderAcceptanceEndTime», пока заказы ещё принимаются.
 * Не используется в корзине при переходе к оформлению.
 */
object OrderAcceptBannerUrgency {

    private const val LAST_HOUR_OF_DAY = 23
    private const val LAST_MINUTE_OF_HOUR = 59

    fun shouldShowEndingSoonBanner(
        snapshot: OrderAcceptStatusSnapshot,
        thresholdMs: Long = ORDER_ACCEPT_ENDING_SOON_THRESHOLD_MS,
    ): Boolean {
        if (!snapshot.isAcceptingOrders) return false
        val endRaw =
            snapshot.orderAcceptanceEndTime?.trim()?.takeIf { it.isNotBlank() } ?: return false
        val serverRaw = snapshot.serverTime?.trim()?.takeIf { it.isNotBlank() } ?: return false
        val remaining = remainingMillisUntilAcceptanceEnd(serverRaw, endRaw) ?: return false
        return remaining in 1 until thresholdMs
    }

    private fun remainingMillisUntilAcceptanceEnd(serverTimeIso: String, endHhMm: String): Long? {
        return try {
            val now = Instant.parse(serverTimeIso)
            val zone = TimeZone.of(ORDER_ACCEPT_LOCAL_TIME_ZONE_ID)
            val localNow = now.toLocalDateTime(zone)
            val (hour, minute) = parseHhMm(endHhMm) ?: return null
            val endLocal = LocalDateTime(localNow.date, LocalTime(hour, minute))
            val endInstant = endLocal.toInstant(zone)
            endInstant.toEpochMilliseconds() - now.toEpochMilliseconds()
        } catch (e: Exception) {
            Napier.w(
                "OrderAcceptBannerUrgency: parse failed serverTime=$serverTimeIso end=$endHhMm",
                e
            )
            null
        }
    }

    private fun parseHhMm(s: String): Pair<Int, Int>? {
        val parts = s.trim().split(':')
        if (parts.size != 2) return null
        val h = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        if (h !in 0..LAST_HOUR_OF_DAY || m !in 0..LAST_MINUTE_OF_HOUR) return null
        return h to m
    }
}

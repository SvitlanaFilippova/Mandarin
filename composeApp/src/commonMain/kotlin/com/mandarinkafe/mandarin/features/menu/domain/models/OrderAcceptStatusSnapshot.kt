package com.mandarinkafe.mandarin.features.menu.domain.models

import com.mandarinkafe.mandarin.util.Constants

data class OrderAcceptStatusSnapshot(
    val isAcceptingOrders: Boolean,
    val closingTime: String?,
    val orderAcceptanceEndTime: String?,
    val serverTime: String?,
    val isClosedForWholeDay: Boolean,
) {
    /** Для строк с «работает до %s», если время не пришло — em dash. Не использовать для `isClosedForWholeDay`. */
    fun closingTimeOrPlaceholder(): String =
        closingTime?.trim()?.takeIf { it.isNotBlank() }
            ?: Constants.CLOSING_TIME_PLACEHOLDER_EM_DASH

    companion object {
        fun accepting(): OrderAcceptStatusSnapshot =
            OrderAcceptStatusSnapshot(
                isAcceptingOrders = true,
                closingTime = null,
                orderAcceptanceEndTime = null,
                serverTime = null,
                isClosedForWholeDay = false,
            )
    }
}

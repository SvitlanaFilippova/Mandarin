package com.mandarinkafe.mandarin.features.menu.domain.models

data class OrderAcceptStatusSnapshot(
    val isAcceptingOrders: Boolean,
    val closingTime: String?,
    val orderAcceptanceEndTime: String?,
    val serverTime: String?,
    val isClosedForWholeDay: Boolean,
) {
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

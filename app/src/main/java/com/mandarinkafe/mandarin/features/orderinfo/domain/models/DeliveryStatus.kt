package com.mandarinkafe.mandarin.features.orderinfo.domain.models

import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus

enum class DeliveryStatus(
    val apiName: String,
) {
    UNCONFIRMED("Unconfirmed"),
    WAIT_COOKING("WaitCooking"),
    READY_FOR_COOKING("ReadyForCooking"),
    COOKING_STARTED("CookingStarted"),
    COOKING_COMPLETED("CookingCompleted"),
    WAITING("Waiting"),
    ON_WAY("OnWay"),
    DELIVERED("Delivered"),
    CLOSED("Closed"),
    CANCELLED("Cancelled")
}

fun DeliveryStatus.toUiStatus(): UiDeliveryStatus {
    return UiDeliveryStatus.entries.firstOrNull { it.apiName == this.apiName }
        ?: error("Unknown DeliveryStatus: $this")
}
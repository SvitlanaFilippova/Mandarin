package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import dev.icerock.moko.resources.StringResource

enum class UiDeliveryStatus(
    val apiName: String,
    val nameRes: StringResource,
    val extraTextResId: StringResource,
    val iconName: String
) {
    UNCONFIRMED(
        "Unconfirmed",
        MR.strings.delivery_status_unconfirmed,
        MR.strings.delivery_status_extra_unconfirmed,
        "ic_call"
    ),
    WAIT_COOKING(
        "WaitCooking",
        MR.strings.delivery_status_wait_cooking,
        MR.strings.delivery_status_extra_wait_cooking,
        "ic_timer"
    ),
    READY_FOR_COOKING(
        "ReadyForCooking",
        MR.strings.delivery_status_wait_cooking,
        MR.strings.delivery_status_extra_wait_cooking,
        "ic_timer"
    ),
    COOKING_STARTED(
        "CookingStarted",
        MR.strings.delivery_status_cooking_started,
        MR.strings.delivery_status_extra_cooking_started,
        "ic_chef_hat"
    ),
    COOKING_COMPLETED(
        "CookingCompleted",
        MR.strings.delivery_status_cooking_completed,
        MR.strings.delivery_status_extra_cooking_completed,
        "ic_hand_meal"
    ),
    WAITING(
        "Waiting",
        MR.strings.delivery_status_waiting,
        MR.strings.delivery_status_extra_waiting,
        "ic_orders"
    ),
    ON_WAY(
        "OnWay",
        MR.strings.delivery_status_on_way,
        MR.strings.delivery_status_extra_on_way,
        "ic_courier"
    ),
    DELIVERED(
        "Delivered",
        MR.strings.delivery_status_delivered,
        MR.strings.delivery_status_extra_delivered,
        "ic_cottage"
    ),
    CLOSED(
        "Closed",
        MR.strings.delivery_status_closed,
        MR.strings.delivery_status_extra_closed,
        "ic_done"
    ),
    CANCELLED(
        "Cancelled",
        MR.strings.delivery_status_cancelled,
        MR.strings.delivery_status_extra_cancelled,
        "ic_no_food"
    )
}

fun DeliveryStatus.toUi(): UiDeliveryStatus {
    return UiDeliveryStatus.entries.firstOrNull { it.apiName == this.apiName }
        ?: error("Unknown DeliveryStatus: $this")
}

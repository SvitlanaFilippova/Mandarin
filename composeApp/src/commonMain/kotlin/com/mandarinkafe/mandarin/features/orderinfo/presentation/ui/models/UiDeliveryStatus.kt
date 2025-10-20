package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class UiDeliveryStatus(
    val apiName: String,
    val nameRes: StringResource,
    val extraTextResId: StringResource,
    val iconRes: ImageResource
) {
    UNCONFIRMED(
        "Unconfirmed",
        MR.strings.delivery_status_unconfirmed,
        MR.strings.delivery_status_extra_unconfirmed,
        MR.images.ic_timer
    ),
    WAIT_COOKING(
        "WaitCooking",
        MR.strings.delivery_status_wait_cooking,
        MR.strings.delivery_status_extra_wait_cooking,
        MR.images.ic_timer
    ),
    READY_FOR_COOKING(
        "ReadyForCooking",
        MR.strings.delivery_status_wait_cooking,
        MR.strings.delivery_status_extra_wait_cooking,
        MR.images.ic_timer
    ),
    COOKING_STARTED(
        "CookingStarted",
        MR.strings.delivery_status_cooking_started,
        MR.strings.delivery_status_extra_cooking_started,
        MR.images.ic_chef_hat
    ),
    COOKING_COMPLETED(
        "CookingCompleted",
        MR.strings.delivery_status_cooking_completed,
        MR.strings.delivery_status_extra_cooking_completed,
        MR.images.ic_hand_meal
    ),
    WAITING(
        "Waiting",
        MR.strings.delivery_status_waiting,
        MR.strings.delivery_status_extra_waiting,
        MR.images.ic_orders
    ),
    ON_WAY(
        "OnWay",
        MR.strings.delivery_status_on_way,
        MR.strings.delivery_status_extra_on_way,
        MR.images.ic_courier
    ),
    DELIVERED(
        "Delivered",
        MR.strings.delivery_status_delivered,
        MR.strings.delivery_status_extra_delivered,
        MR.images.ic_cottage
    ),
    CLOSED(
        "Closed",
        MR.strings.delivery_status_closed,
        MR.strings.delivery_status_extra_closed,
        MR.images.ic_check
    ),
    CANCELLED(
        "Cancelled",
        MR.strings.delivery_status_cancelled,
        MR.strings.delivery_status_extra_cancelled,
        MR.images.ic_no_food
    )
}

fun DeliveryStatus.toUi(): UiDeliveryStatus {
    return UiDeliveryStatus.entries.firstOrNull { it.apiName == this.apiName }
        ?: error("Unknown DeliveryStatus: $this")
}


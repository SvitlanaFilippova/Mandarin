package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class UiDeliveryStatus(
    val nameRes: StringResource,
    val extraTextResId: StringResource?,
    val iconRes: ImageResource,
) {
    UNCONFIRMED(
        MR.strings.delivery_status_unconfirmed,
        MR.strings.delivery_status_extra_unconfirmed,
        MR.images.ic_timer
    ),
    WAIT_COOKING(
        MR.strings.delivery_status_wait_cooking,
        MR.strings.delivery_status_extra_wait_cooking,
        MR.images.ic_timer
    ),
    READY_FOR_COOKING(
        MR.strings.delivery_status_wait_cooking,
        MR.strings.delivery_status_extra_wait_cooking,
        MR.images.ic_timer
    ),
    COOKING_STARTED(
        MR.strings.delivery_status_cooking_started,
        MR.strings.delivery_status_extra_cooking_started,
        MR.images.ic_chef_hat
    ),
    COOKING_COMPLETED(
        MR.strings.delivery_status_cooking_completed,
        MR.strings.delivery_status_extra_cooking_completed,
        MR.images.ic_hand_meal
    ),
    WAITING(
        MR.strings.delivery_status_waiting,
        MR.strings.delivery_status_extra_waiting,
        MR.images.ic_orders
    ),

    ON_WAY(
        MR.strings.delivery_status_on_way,
        MR.strings.delivery_status_extra_on_way,
        MR.images.ic_courier
    ),
    DELIVERED(
        MR.strings.delivery_status_delivered,
        MR.strings.delivery_status_extra_delivered,
        MR.images.ic_cottage
    ),
    CLOSED(
        MR.strings.delivery_status_closed,
        MR.strings.delivery_status_extra_closed,
        MR.images.ic_check
    ),
    CANCELLED(
        MR.strings.delivery_status_cancelled,
        null,
        MR.images.ic_no_food
    ),
    CREATION_ERROR(
        MR.strings.order_creation_status_error,
        MR.strings.order_creation_status_error_extra,
        MR.images.ic_error
    ),
    COOKING_COMPLETED_SELFPICKUP(
        MR.strings.delivery_status_cooking_completed,
        MR.strings.delivery_status_extra_cooking_completed_selfpickup,
        MR.images.ic_hand_meal
    ),
    WAITING_SELFPICKUP(
        MR.strings.delivery_status_waiting,
        MR.strings.delivery_status_extra_waiting_selfpickup,
        MR.images.ic_orders
    ),
}


private fun DeliveryStatus.baseUi(): UiDeliveryStatus =
    when (this) {
        DeliveryStatus.UNCONFIRMED -> UiDeliveryStatus.UNCONFIRMED
        DeliveryStatus.WAIT_COOKING -> UiDeliveryStatus.WAIT_COOKING
        DeliveryStatus.READY_FOR_COOKING -> UiDeliveryStatus.READY_FOR_COOKING
        DeliveryStatus.COOKING_STARTED -> UiDeliveryStatus.COOKING_STARTED
        DeliveryStatus.COOKING_COMPLETED -> UiDeliveryStatus.COOKING_COMPLETED
        DeliveryStatus.WAITING -> UiDeliveryStatus.WAITING
        DeliveryStatus.ON_WAY -> UiDeliveryStatus.ON_WAY
        DeliveryStatus.DELIVERED -> UiDeliveryStatus.DELIVERED
        DeliveryStatus.CLOSED -> UiDeliveryStatus.CLOSED
        DeliveryStatus.CANCELLED -> UiDeliveryStatus.CANCELLED
    }

fun DeliveryStatus.toUi(isDelivery: Boolean? = null): UiDeliveryStatus {
    if (isDelivery == false) {
        when (this) {
            DeliveryStatus.COOKING_COMPLETED ->
                return UiDeliveryStatus.COOKING_COMPLETED_SELFPICKUP

            DeliveryStatus.WAITING ->
                return UiDeliveryStatus.WAITING_SELFPICKUP

            else -> {}
        }
    }

    return baseUi()
}

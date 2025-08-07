package com.mandarinkafe.mandarin.features.orderinfo.domain.models

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R

enum class DeliveryStatus(
    val apiName: String,
    @StringRes val labelResId: Int
) {
    UNCONFIRMED("Unconfirmed", R.string.delivery_status_unconfirmed),
    WAIT_COOKING("WaitCooking", R.string.delivery_status_wait_cooking),
    READY_FOR_COOKING("ReadyForCooking", R.string.delivery_status_ready_for_cooking),
    COOKING_STARTED("CookingStarted", R.string.delivery_status_cooking_started),
    COOKING_COMPLETED("CookingCompleted", R.string.delivery_status_cooking_completed),
    WAITING("Waiting", R.string.delivery_status_waiting),
    ON_WAY("OnWay", R.string.delivery_status_on_way),
    DELIVERED("Delivered", R.string.delivery_status_delivered),
    CLOSED("Closed", R.string.delivery_status_closed),
    CANCELLED("Cancelled", R.string.delivery_status_cancelled)
}
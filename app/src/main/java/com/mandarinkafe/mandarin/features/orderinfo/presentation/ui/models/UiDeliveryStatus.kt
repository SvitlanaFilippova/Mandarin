package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.R

@Stable
enum class UiDeliveryStatus(
    val apiName: String,
    @StringRes val labelResId: Int,
    @DrawableRes val iconResID: Int
) {
    UNCONFIRMED("Unconfirmed", R.string.delivery_status_unconfirmed, R.drawable.ic_call),
    WAIT_COOKING("WaitCooking", R.string.delivery_status_wait_cooking, R.drawable.ic_timer),
    READY_FOR_COOKING(
        "ReadyForCooking",
        R.string.delivery_status_ready_for_cooking,
        R.drawable.ic_timer
    ),
    COOKING_STARTED(
        "CookingStarted",
        R.string.delivery_status_cooking_started,
        R.drawable.ic_chef_hat
    ),
    COOKING_COMPLETED(
        "CookingCompleted",
        R.string.delivery_status_cooking_completed,
        R.drawable.ic_hand_meal
    ),
    WAITING("Waiting", R.string.delivery_status_waiting, R.drawable.ic_orders),
    ON_WAY("OnWay", R.string.delivery_status_on_way, R.drawable.ic_courier),
    DELIVERED("Delivered", R.string.delivery_status_delivered, R.drawable.ic_cottage),
    CLOSED("Closed", R.string.delivery_status_closed, R.drawable.ic_done),
    CANCELLED("Cancelled", R.string.delivery_status_cancelled, R.drawable.ic_no_food)
}
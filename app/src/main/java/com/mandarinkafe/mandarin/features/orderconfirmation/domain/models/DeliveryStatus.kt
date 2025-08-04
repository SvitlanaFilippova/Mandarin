package com.mandarinkafe.mandarin.features.orderconfirmation.domain.models

enum class DeliveryStatus(val apiName: String) {
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
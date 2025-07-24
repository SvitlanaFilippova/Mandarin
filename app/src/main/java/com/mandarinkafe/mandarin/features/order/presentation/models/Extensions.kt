package com.mandarinkafe.mandarin.features.order.presentation.models

fun UiAddress.getDetails(): String {
    if (isPrivateHouse) return ""

    val parts = listOfNotNull(
        apartmentNumber.takeIf { it.isNotBlank() }?.let { "кв. $it" },
        entrance.takeIf { it.isNotBlank() }?.let { "подъезд $it" },
        intercom.takeIf { it.isNotBlank() }?.let { "домофон $it" },
        floor.takeIf { it.isNotBlank() }?.let { "этаж $it" }
    )

    return parts.joinToString(separator = ", ")
}

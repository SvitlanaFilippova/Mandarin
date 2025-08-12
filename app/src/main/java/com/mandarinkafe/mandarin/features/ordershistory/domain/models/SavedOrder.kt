package com.mandarinkafe.mandarin.features.ordershistory.domain.models

import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus

data class SavedOrder(
    val id: String, // внутренний ID заказа
    val timestamp: Long,
    val whenCreated: String = "",
    val orderType: String = "", // доставка или самовывоз
    val addressLine1: String = "",
    val addressDetails: String = "",
    val mealNames: String = "",
    val status: UiDeliveryStatus? = null
)
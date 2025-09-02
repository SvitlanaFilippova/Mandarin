package com.mandarinkafe.mandarin.features.orderinfo.data.network

data class CancelOrderRequest(
    val organizationId: String,
    val orderId: String
)
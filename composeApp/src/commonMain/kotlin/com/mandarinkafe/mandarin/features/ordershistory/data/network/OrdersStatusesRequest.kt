package com.mandarinkafe.mandarin.features.ordershistory.data.network

import kotlinx.serialization.Serializable

@Serializable
data class OrdersStatusesRequest(
    val orderIds: List<String>,
)


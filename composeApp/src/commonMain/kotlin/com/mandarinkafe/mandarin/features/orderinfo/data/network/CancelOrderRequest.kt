package com.mandarinkafe.mandarin.features.orderinfo.data.network

import kotlinx.serialization.Serializable

@Serializable
data class CancelOrderRequest(
    val organizationId: String,
    val orderId: String,
    val cancelCauseId: String? = null,
    val cancelComment: String? = null,
)






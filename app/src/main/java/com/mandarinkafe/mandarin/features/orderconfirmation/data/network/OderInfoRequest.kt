package com.mandarinkafe.mandarin.features.orderconfirmation.data.network

data class OderInfoRequest(
    val organizationId: String,
    val orderIds: List<String>
)

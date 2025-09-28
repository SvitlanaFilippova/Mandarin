package com.mandarinkafe.mandarin.features.orderinfo.data.network

data class OderInfoRequest(
    val organizationId: String,
    val orderIds: List<String>
)

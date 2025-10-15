package com.mandarinkafe.mandarin.features.orderinfo.data.network

import kotlinx.serialization.Serializable

@Serializable
data class OderInfoRequest(
    val organizationId: String,
    val orderIds: List<String>
)





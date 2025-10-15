package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeletionMethodDto(
    val id: String? = null,
    val comment: String? = null,
    val removalType: RemovalTypeDto? = null,
)





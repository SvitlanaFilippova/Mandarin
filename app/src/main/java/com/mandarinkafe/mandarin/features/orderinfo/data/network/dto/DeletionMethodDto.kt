package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeletionMethodDto(
    val id: String,
    val comment: String?,
    val removalType: RemovalTypeDto?
)
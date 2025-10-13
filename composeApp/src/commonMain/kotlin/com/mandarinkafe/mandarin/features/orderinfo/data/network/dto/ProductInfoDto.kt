package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductInfoDto(
    val id: String,
    val name: String
)

package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeletionInfoDto(
    val deletionMethod: DeletionMethodDto? = null,
)





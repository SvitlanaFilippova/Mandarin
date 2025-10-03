package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingModifierDto(
    val product: ProductInfoDto,
    val amount: Double? = null,
    val price: Double,
    val resultSum: Double? = null,
    val productGroup: ProductInfoDto,
)
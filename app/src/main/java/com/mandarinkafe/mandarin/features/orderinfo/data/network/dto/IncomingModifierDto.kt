package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingModifierDto(
    val product: ProductInfoDto,
    val amount: Double,
    val price: Double,
    val resultSum: Double?,
    val productGroup: ProductInfoDto
)
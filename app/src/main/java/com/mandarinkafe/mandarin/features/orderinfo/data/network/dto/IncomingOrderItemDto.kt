package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingOrderItemDto(
    val product: ProductInfoDto,
    val amount: Double? = null,
    val modifiers: List<IncomingModifierDto> = emptyList(),
    val price: Double,
    val resultSum: Double? = null,
    val positionId: String? = null,
    val deleted: DeletionInfoDto? = null,
    val comment: String?? = null,
)
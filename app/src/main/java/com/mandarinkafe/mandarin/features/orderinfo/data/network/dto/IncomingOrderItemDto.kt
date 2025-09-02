package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

data class IncomingOrderItemDto(
    val product: ProductInfoDto,
    val amount: Double,
    val modifiers: List<IncomingModifierDto>?,
    val price: Double,
    val resultSum: Double?,
    val positionId: String?,
    val deleted: DeletionInfoDto?,
    val comment: String?
)
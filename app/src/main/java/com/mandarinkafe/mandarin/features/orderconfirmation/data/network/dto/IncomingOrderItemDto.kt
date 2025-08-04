package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

data class IncomingOrderItemDto(
    val product: ProductInfoDto,
    val amount: Double,
    val modifiers: List<IncomingModifierDto>?,
    val price: Double,
    val positionId: String?, // WTF?
    val deleted: DeletionInfoDto?,
    val comment: String?
)

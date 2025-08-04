package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

data class IncomingModifierDto(
    val product: ProductInfoDto,
    val amount: Double,
    val price: Double,
    val productGroup: ProductInfoDto
)
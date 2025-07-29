package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class DeliveryPoint(
    val address: AddressDto,
    val comment: String?,
)
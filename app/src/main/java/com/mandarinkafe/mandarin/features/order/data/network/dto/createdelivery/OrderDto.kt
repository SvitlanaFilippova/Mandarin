package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class OrderDto(
    val phone: String,
    val orderServiceType: String,
    val deliveryPoint: DeliveryPoint?,
    val comment: String,
    val customer: Customer,
    val items: List<Item>,
    val payments: List<Payment>,
)
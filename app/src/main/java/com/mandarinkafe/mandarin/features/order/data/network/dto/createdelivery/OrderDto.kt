package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class OrderDto(
    val phone: String, // Must begin with symbol "+" and must be at least 8 digits.
    val orderServiceType: String, //Enum: "DeliveryByCourier" "DeliveryByClient"
    val deliveryPoint: DeliveryPoint,
    val comment: String,
    val customer: Customer,
    val items: List<Item>,
    val payments: List<Payment>,
)
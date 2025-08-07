package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.core.domain.models.Address

data class OutgoingOrder(
    val name: String,
    val phone: String,
    val deliveryType: DeliveryType,
    val chosenAddress: Address?,
    val paymentType: PaymentType,
    val comment: String,
    val items: List<OutgoingOrderItem>,
    val discountCategory: Int,
    val totalOrderSum: Double,
    val deliveryRealCost: Int,
    val deliveryZoneID: Int?,
)

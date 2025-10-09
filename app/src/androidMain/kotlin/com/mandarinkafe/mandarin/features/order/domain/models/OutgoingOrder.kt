package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem

data class OutgoingOrder(
    val name: String,
    val phone: String,
    val deliveryType: DeliveryType,
    val chosenAddress: Address?,
    val paymentType: PaymentType,
    val comment: String,
    val items: List<CartItem>,
    val discountPercent: Int,
    val totalOrderSum: Double,
    val deliveryRealCost: Int,
    val deliveryZoneID: Int?,
    val discountTypeId: String?
)

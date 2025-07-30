package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal

data class Order(
    val name: String,
    val phone: String,
    val deliveryType: DeliveryType,
    val chosenAddress: Address?,
    val paymentType: PaymentType,
    val comment: String,
    val cartItems: Map<CustomizedMeal, Int>,
    val deliveryRealCost: Int,
    val totalOrderSum: Double,
    val discountCategory: Int,
)

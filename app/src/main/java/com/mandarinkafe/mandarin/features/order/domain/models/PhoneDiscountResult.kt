package com.mandarinkafe.mandarin.features.order.domain.models

data class PhoneDiscountResult(
    val phone: String,
    val discountSize: Int,
    val shouldUpdate: Boolean
)
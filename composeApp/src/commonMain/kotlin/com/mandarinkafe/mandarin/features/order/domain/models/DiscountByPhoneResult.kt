package com.mandarinkafe.mandarin.features.order.domain.models

data class DiscountByPhoneResult(
    val discountSize: Int,
    val shouldUpdate: Boolean,
)
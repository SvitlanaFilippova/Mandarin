package com.mandarinkafe.mandarin.features.order.domain.models

data class DiscountByPhoneResult(
    val discountSize: Int,
    val discountId: String? = null,
    val shouldUpdate: Boolean
)
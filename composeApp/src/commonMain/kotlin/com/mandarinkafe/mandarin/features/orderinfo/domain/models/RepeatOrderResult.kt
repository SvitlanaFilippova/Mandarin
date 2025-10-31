package com.mandarinkafe.mandarin.features.orderinfo.domain.models

import com.mandarinkafe.mandarin.core.domain.models.CartItem

data class RepeatOrderResult(
    val cartItems: List<CartItem>,
    val hasInvalidItems: Boolean,
)
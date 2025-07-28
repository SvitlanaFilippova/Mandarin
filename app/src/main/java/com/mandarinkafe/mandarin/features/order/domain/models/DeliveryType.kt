package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.R

enum class DeliveryType(val nameRes: Int) {
    DELIVERY(R.string.shipping),
    SELF_PICKUP(R.string.shipping_self_pickup),
}


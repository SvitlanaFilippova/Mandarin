package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.R

enum class DeliveryType(val nameRes: Int) {
    APARTMENT(R.string.shipping_to_apartment),
    PRIVATE_HOUSE(R.string.shipping_to_private_house),
    SELF_PICKUP(R.string.shipping_self_pickup),
}


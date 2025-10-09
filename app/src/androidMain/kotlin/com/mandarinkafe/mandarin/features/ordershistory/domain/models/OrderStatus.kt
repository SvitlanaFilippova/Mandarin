package com.mandarinkafe.mandarin.features.ordershistory.domain.models

import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus

data class OrderStatus(val orderId: String, val status: DeliveryStatus?)

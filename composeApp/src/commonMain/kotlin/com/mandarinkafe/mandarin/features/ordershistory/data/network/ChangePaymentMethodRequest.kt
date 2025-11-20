package com.mandarinkafe.mandarin.features.ordershistory.data.network

import kotlinx.serialization.Serializable

@Serializable
data class ChangePaymentMethodRequest(
    val payment_method_code: String
)


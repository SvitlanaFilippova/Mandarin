package com.mandarinkafe.mandarin.features.ordershistory.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePaymentMethodRequest(
    @SerialName("payment_method_code")
    val paymentMethodCode: String,
)


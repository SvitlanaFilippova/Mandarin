package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaymentTypeDto(
    val kind: String?, // Enum: "Unknown" "Cash" "Card" "Credit" "Writeoff" "Voucher" "External" "SmartSale" "Sberbank" "Trpos"
    val name: String?,
)






package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

data class PaymentTypeDto(
    val kind: String?, // Enum: "Unknown" "Cash" "Card" "Credit" "Writeoff" "Voucher" "External" "SmartSale" "Sberbank" "Trpos"
    val name: String?
)
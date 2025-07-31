package com.mandarinkafe.mandarin.core.data.dto.order

data class CancelInfo(
    val cause: Cause,
    val comment: String,
    val whenCancelled: String
)
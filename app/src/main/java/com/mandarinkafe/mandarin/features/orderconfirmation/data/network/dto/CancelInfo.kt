package com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.order.Cause

data class CancelInfo(
    val cause: Cause,
    val comment: String,
    val whenCancelled: String
)
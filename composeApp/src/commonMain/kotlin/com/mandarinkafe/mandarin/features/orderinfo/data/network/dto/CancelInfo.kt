package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.order.Cause
import kotlinx.serialization.Serializable

@Serializable
data class CancelInfo(
    val cause: Cause? = null,
    val comment: String? = null,
    val whenCancelled: String? = null,
)






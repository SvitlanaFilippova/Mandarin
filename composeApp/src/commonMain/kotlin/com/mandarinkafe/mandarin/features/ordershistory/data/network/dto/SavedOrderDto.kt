package com.mandarinkafe.mandarin.features.ordershistory.data.network.dto

import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedOrderDto(
    val id: String,
    val number: String = "",
    val timestamp: Long,
    val whenCreated: String = "",
    val orderType: String = "", // "DELIVERY" or "SELF_PICKUP", empty string if not set
    val addressLine1: String = "",
    val addressDetails: String = "",
    val mealNames: String = "",
    @SerialName("payment_method_code")
    val paymentMethodCode: String? = null,
    val mealIds: List<String> = emptyList(),
    val status: String? = null,
    val creationStatus: String? = null,
    val errorInfo: ErrorInfoDto? = null,
)

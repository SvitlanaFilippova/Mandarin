package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype

import kotlinx.serialization.Serializable

@Serializable
data class PaymentTypesRequest(
    val organizationIds: List<String>
)





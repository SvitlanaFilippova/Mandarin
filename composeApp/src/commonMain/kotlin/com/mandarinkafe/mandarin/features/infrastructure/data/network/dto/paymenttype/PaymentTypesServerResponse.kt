package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class PaymentTypesServerResponse(
    val paymentTypes: List<PaymentTypeServer>,
) : Response()



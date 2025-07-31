package com.mandarinkafe.mandarin.features.order.data.network.dto.paymenttype

import com.mandarinkafe.mandarin.core.data.dto.Response

data class PaymentTypesResponse(
    val paymentTypes: List<PaymentTypeIiko>
) : Response()
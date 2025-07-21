package com.mandarinkafe.mandarin.features.order.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

data class NominatimResponse(
    val lat: String,
    val lon: String,
) : Response()
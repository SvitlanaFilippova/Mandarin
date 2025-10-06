package com.mandarinkafe.mandarin.features.address.address.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryZonesResponse(
    val data: List<DeliveryZoneDto>
) : Response()
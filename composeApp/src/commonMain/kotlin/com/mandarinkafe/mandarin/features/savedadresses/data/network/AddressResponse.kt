package com.mandarinkafe.mandarin.features.savedadresses.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.savedadresses.data.network.dto.AddressDto
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(val data: List<AddressDto>? = null) : Response()


package com.mandarinkafe.mandarin.features.savedadresses.data.network

import com.mandarinkafe.mandarin.features.savedadresses.data.network.dto.AddressDto
import kotlinx.serialization.Serializable

@Serializable
data class AddressUpdateRequest(val data: AddressDto)


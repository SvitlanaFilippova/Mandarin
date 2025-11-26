package com.mandarinkafe.mandarin.features.more.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class AppStoresResponse(
    val data: List<AppStoreDto> = emptyList(),
) : Response()

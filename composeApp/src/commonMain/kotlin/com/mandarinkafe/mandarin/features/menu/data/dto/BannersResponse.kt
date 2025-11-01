package com.mandarinkafe.mandarin.features.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class BannersResponse(
    val data: List<BannerDto>,
) : Response()



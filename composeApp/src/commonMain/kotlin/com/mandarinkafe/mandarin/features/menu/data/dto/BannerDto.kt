package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BannerDto(
    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("target_name")
    val targetName: String? = null,

    val id: Int? = null,
)






package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("id")
    val id: String?,
    @SerialName("phone")
    val phone: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("is_verified")
    val isVerified: Boolean?,
)


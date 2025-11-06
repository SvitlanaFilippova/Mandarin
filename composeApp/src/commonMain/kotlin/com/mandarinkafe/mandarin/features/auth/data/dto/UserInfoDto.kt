package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("phone")
    val phone: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("email")
    val email: String?,
)


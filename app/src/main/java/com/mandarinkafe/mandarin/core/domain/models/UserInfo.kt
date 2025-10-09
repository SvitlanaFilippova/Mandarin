package com.mandarinkafe.mandarin.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String,
    val phone: String
)
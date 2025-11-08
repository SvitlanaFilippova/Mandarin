package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySmsCodeRequest(
    val phone: String,
    val code: String,
    @SerialName("device_name")
    val deviceName: String,
)






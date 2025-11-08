package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationStatusByPhoneRequest(
    @SerialName("phone")
    val phone: String,
    @SerialName("device_name")
    val deviceName: String,
)
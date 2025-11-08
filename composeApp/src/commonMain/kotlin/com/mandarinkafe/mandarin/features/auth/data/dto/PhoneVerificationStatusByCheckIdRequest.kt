package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationStatusByCheckIdRequest(
    @SerialName("check_id")
    val checkId: String,
    @SerialName("device_name")
    val deviceName: String,
)


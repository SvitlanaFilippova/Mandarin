package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationDataDto(
    @SerialName("check_id")
    val checkId: String,
    @SerialName("call_phone")
    val phoneToCall: String,
    @SerialName("call_phone_pretty")
    val phoneToCallPretty: String,
)

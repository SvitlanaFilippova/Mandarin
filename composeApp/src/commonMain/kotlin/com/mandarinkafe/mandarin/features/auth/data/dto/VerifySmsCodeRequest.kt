package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifySmsCodeRequest(
    val phone: String,
    val code: String,
)




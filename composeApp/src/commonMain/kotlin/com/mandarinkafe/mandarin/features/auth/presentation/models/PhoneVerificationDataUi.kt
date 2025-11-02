package com.mandarinkafe.mandarin.features.auth.presentation.models

import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationDataUi
    (
    val checkId: String,
    val phoneToCall: String,
    val userPhone: String,
)

fun PhoneVerificationData.toUi(userPhone: String) = PhoneVerificationDataUi(
    checkId = checkId,
    phoneToCall = phoneToCall,
    userPhone = userPhone
)

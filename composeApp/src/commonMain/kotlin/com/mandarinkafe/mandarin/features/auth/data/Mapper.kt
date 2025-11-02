package com.mandarinkafe.mandarin.features.auth.data

import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusDto
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus

object Mapper {
    fun PhoneVerificationStatusDto.toDomain() = PhoneVerificationStatus(
        phone = phone,
        isVerified = isVerified,
        checkId = checkId,
        status = status,
        verifiedAt = verifiedAt,
        shouldStopPolling = shouldStopPolling,
        expiresInSeconds = expiresInSeconds
    )
}

fun PhoneVerificationDataDto.toDomain() = PhoneVerificationData(
    checkId = checkId,
    phoneToCall = phoneToCall
)
package com.mandarinkafe.mandarin.features.auth.data

import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusDto
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.VerificationFailReason
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeDataDto
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult

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

    fun PhoneVerificationDataDto.toDomain() = PhoneVerificationData(
        checkId = checkId,
        phoneToCall = phoneToCall
    )

    fun SmsVerificationDataDto.toDomain() = SmsVerificationData(
        status = status,
        expiresIn = expiresIn
    )

    fun VerifySmsCodeDataDto.toDomain() = VerifySmsCodeResult(
        isVerified = isVerified,
        reason = VerificationFailReason.fromServerName(reason)
    )


}
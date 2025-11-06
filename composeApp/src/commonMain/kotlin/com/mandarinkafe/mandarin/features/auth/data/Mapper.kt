package com.mandarinkafe.mandarin.features.auth.data

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusDto
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.VerificationFailReason
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeDataDto
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.util.Constants

object Mapper {
    fun PhoneVerificationDataDto.toDomain() = PhoneVerificationData(
        checkId = checkId,
        phoneToCall = phoneToCall,
        expiresInSeconds = expiresInSeconds
    )

    fun PhoneVerificationStatusDto.toDomain() = PhoneVerificationStatus(
        phone = phone,
        isVerified = isVerified,
        checkId = checkId,
        status = status,
        verifiedAt = verifiedAt,
        shouldStopPolling = shouldStopPolling,
        expiresInSeconds = expiresInSeconds,
        tokens = extractTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = tokenType
        )
    )

    fun SmsVerificationDataDto.toDomain() = SmsVerificationData(
        status = status,
        expiresIn = expiresIn
    )

    fun VerifySmsCodeDataDto.toDomain() = VerifySmsCodeResult(
        isVerified = isVerified,
        reason = VerificationFailReason.fromServerName(reason),
        tokens = extractTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = tokenType
        )
    )

    private fun extractTokens(
        accessToken: String?,
        refreshToken: String?,
        tokenType: String?,
    ): AuthTokens? {
        return if (accessToken != null && refreshToken != null) {
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = tokenType ?: Constants.BEARER_TOKEN_TYPE
            )
        } else {
            null
        }
    }
}
package com.mandarinkafe.mandarin.features.auth.domain.models

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import dev.icerock.moko.resources.StringResource

/**
 * Domain модель для результата проверки SMS кода
 */
data class VerifySmsCodeResult(
    val isVerified: Boolean,
    val reason: StringResource?,
    val tokens: AuthTokens?,
)






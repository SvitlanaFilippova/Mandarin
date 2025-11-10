package com.mandarinkafe.mandarin.features.auth.data.dto

import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.StringResource

enum class VerificationFailReason(val serverName: String, val uiReason: StringResource) {
    INVALID_FORMAT("invalid_format", MR.strings.sms_error_invalid_format),
    CODE_EXPIRED("code_expired", MR.strings.sms_error_code_expired),
    CODE_NOT_FOUND("code_not_found", MR.strings.sms_error_code_not_found),
    INVALID_CODE("invalid_code", MR.strings.sms_error_invalid_code);

    companion object {
        /**
         * Маппинг строкового значения reason от сервера в StringResource
         * @param serverReason строковое значение от сервера (например, "invalid_code")
         * @return соответствующий StringResource или null, если reason не распознан
         */
        fun fromServerName(serverReason: String?): StringResource? {
            if (serverReason == null) return null
            return entries.find { it.serverName == serverReason }?.uiReason
        }
    }
}
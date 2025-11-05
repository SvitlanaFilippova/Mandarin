package com.mandarinkafe.mandarin.features.auth.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

/**
 * Response-обёртка для ответа /auth/verify_sms
 */
class VerifySmsCodeResponse(
    val data: VerifySmsCodeDataDto? = null,
) : Response()




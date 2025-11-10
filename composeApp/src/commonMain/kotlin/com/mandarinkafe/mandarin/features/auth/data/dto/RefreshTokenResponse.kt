package com.mandarinkafe.mandarin.features.auth.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

class RefreshTokenResponse(
    val data: RefreshTokenDataDto? = null,
) : Response()


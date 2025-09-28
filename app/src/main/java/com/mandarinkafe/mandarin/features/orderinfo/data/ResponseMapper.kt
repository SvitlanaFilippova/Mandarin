package com.mandarinkafe.mandarin.features.orderinfo.data

import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo

fun ErrorInfoDto.toDomain() = ErrorInfo(
    code = code,
    message = message,
    errorReason = errorReason,
    additionalData = additionalData
)
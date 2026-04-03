package com.mandarinkafe.mandarin.features.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

/** Обёртка результата [ServerApi.getOrderAcceptStatus]: тело ответа — плоский JSON [OrderAcceptStatusDto]. */
class OrderAcceptStatusHttpResponse(
    val payload: OrderAcceptStatusDto,
) : Response()

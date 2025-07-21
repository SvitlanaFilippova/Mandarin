package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea
import com.yandex.mapkit.geometry.Point

interface GetDeliveryZoneUseCase {
    operator fun invoke(location: Point): List<DeliveryArea>
}
package com.mandarinkafe.mandarin.features.address.map.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

interface GetDeliveryZoneUseCase {
    operator fun invoke(location: GeoPoint): DeliveryArea?
}
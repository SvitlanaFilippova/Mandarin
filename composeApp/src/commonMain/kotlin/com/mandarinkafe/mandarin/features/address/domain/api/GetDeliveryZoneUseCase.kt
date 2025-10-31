package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

interface GetDeliveryZoneUseCase {
    suspend operator fun invoke(location: GeoPoint?): DeliveryZone?
}
package com.mandarinkafe.mandarin.features.address.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone

interface DeliveryAreaRepository {
   suspend fun getAllAreas(): List<DeliveryZone>
}
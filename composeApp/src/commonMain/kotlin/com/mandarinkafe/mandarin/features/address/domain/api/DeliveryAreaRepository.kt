package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.util.Resource

interface DeliveryAreaRepository {
    suspend fun getAllAreas(): Resource<List<DeliveryZone>>
}
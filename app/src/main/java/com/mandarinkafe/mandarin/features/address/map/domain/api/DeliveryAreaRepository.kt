package com.mandarinkafe.mandarin.features.address.map.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea

interface DeliveryAreaRepository {
    fun getAllAreas(): List<DeliveryArea>
}
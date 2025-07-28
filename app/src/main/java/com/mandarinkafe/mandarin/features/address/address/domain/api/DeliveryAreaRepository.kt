package com.mandarinkafe.mandarin.features.address.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea

interface DeliveryAreaRepository {
    fun getAllAreas(): List<DeliveryArea>
}
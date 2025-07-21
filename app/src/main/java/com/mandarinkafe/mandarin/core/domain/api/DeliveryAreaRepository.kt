package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea

interface DeliveryAreaRepository {
    fun getAllAreas(): List<DeliveryArea>
}
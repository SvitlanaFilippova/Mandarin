package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.features.address.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface GetAddressByPointUseCase {
    fun observeAddress(): StateFlow<Resource<String>>
    suspend operator fun invoke(point: GeoPoint)
}
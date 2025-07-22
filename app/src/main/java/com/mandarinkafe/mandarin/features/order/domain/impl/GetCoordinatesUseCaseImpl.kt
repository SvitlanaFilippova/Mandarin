package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.order.domain.api.GeocodingRepository
import com.mandarinkafe.mandarin.features.order.domain.api.GetCoordinatesUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Success
import com.yandex.mapkit.geometry.Point

class GetCoordinatesUseCaseImpl(private val repository: GeocodingRepository) :
    GetCoordinatesUseCase {
    override suspend fun invoke(address: String): Resource<Point> {
        val result = repository.getCoordinatesFromAddress(address)

        return when (result) {
            is Success -> {
                val data = result.data
                if (data != null)
                    Success(data) else Resource.ErrorEmptyData()
            }

            else -> {
                result
            }
        }
    }
}
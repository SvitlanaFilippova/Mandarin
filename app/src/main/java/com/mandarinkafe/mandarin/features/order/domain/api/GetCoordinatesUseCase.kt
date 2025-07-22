package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.geometry.Point

interface GetCoordinatesUseCase {
    suspend operator fun invoke(address: String): Resource<Point>
}
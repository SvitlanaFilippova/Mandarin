package com.mandarinkafe.mandarin.features.address.textsearch

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

data class SearchResponseItem(
    val point: GeoPoint,
    val address: com.yandex.mapkit.GeoObject?,
)

package com.mandarinkafe.mandarin.features.address.textsearch

import com.mandarinkafe.mandarin.features.address.map.domain.models.GeoPoint

data class SearchResponseItem(
    val point: GeoPoint,
    val address: com.yandex.mapkit.GeoObject?,
)

package com.mandarinkafe.mandarin.util.presentation

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LONGITUDE

fun createDefaultPoint() = GeoPoint(
    latitude = MANDARIN_CAFE_LATITUDE,
    longitude = MANDARIN_CAFE_LONGITUDE
)
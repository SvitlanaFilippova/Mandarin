package com.mandarinkafe.mandarin.core.data.dto.map

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.yandex.mapkit.geometry.Point

data class PointResponse(val point: Point?) : Response()
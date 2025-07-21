package com.mandarinkafe.mandarin.features.delivery.impl

import com.mandarinkafe.mandarin.core.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area1
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area10
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area11
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area12
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area2
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area3
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area4
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area5
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area6
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area7
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area8
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates.Area9
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA10_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA11_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA12_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA1_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA2_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA3_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA4_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA5_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA6_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA7_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA8_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.AREA9_ID
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA1
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA10
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA11
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA12
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA2
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA3
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA4
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA5
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA6
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA7
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA8
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_AREA9
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA1
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA10
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA11
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA12
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA2
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA3
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA4
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA5
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA6
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA7
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA8
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants.DELIVERY_PRICE_FREE_AREA9

class DeliveryAreaRepositoryImpl : DeliveryAreaRepository {

    private val areas = listOf(
        DeliveryArea(
            id = AREA1_ID,
            polygon = Area1,
            parentArea = null,
            deliveryPrice = DELIVERY_PRICE_AREA1,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA1
        ),
        DeliveryArea(
            id = AREA2_ID,
            polygon = Area2,
            parentArea = Area1,
            deliveryPrice = DELIVERY_PRICE_AREA2,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA2
        ),
        DeliveryArea(
            id = AREA3_ID,
            polygon = Area3,
            parentArea = Area2,
            deliveryPrice = DELIVERY_PRICE_AREA3,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA3
        ),
        DeliveryArea(
            id = AREA4_ID,
            polygon = Area4,
            parentArea = Area3,
            deliveryPrice = DELIVERY_PRICE_AREA4,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA4
        ),
        DeliveryArea(
            id = AREA5_ID,
            polygon = Area5,
            parentArea = Area4,
            deliveryPrice = DELIVERY_PRICE_AREA5,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA5
        ),
        DeliveryArea(
            id = AREA6_ID,
            polygon = Area6,
            parentArea = Area5,
            deliveryPrice = DELIVERY_PRICE_AREA6,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA6
        ),
        DeliveryArea(
            id = AREA7_ID,
            polygon = Area7,
            parentArea = Area6,
            deliveryPrice = DELIVERY_PRICE_AREA7,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA7
        ),
        DeliveryArea(
            id = AREA8_ID,
            polygon = Area8,
            parentArea = Area7,
            deliveryPrice = DELIVERY_PRICE_AREA8,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA8
        ),
        DeliveryArea(
            id = AREA9_ID,
            polygon = Area9,
            parentArea = Area8,
            deliveryPrice = DELIVERY_PRICE_AREA9,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA9
        ),
        DeliveryArea(
            id = AREA10_ID,
            polygon = Area10,
            parentArea = Area9,
            deliveryPrice = DELIVERY_PRICE_AREA10,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA10
        ),
        DeliveryArea(
            id = AREA11_ID,
            polygon = Area11,
            parentArea = Area10,
            deliveryPrice = DELIVERY_PRICE_AREA11,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA11
        ),
        DeliveryArea(
            id = AREA12_ID,
            polygon = Area12,
            parentArea = Area11,
            deliveryPrice = DELIVERY_PRICE_AREA12,
            freeDeliveryThreshold = DELIVERY_PRICE_FREE_AREA12
        )
    )

    override fun getAllAreas(): List<DeliveryArea> = areas
}
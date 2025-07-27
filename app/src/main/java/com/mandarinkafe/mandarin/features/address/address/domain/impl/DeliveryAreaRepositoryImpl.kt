package com.mandarinkafe.mandarin.features.address.address.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.address.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.delivery.data.Coordinates
import com.mandarinkafe.mandarin.features.delivery.data.DeliveryConstants

class DeliveryAreaRepositoryImpl : DeliveryAreaRepository {

    private val areas = listOf(
        DeliveryArea(
            id = DeliveryConstants.AREA1_ID,
            polygon = Coordinates.Area1.map { it.toGeoPoint() },
            parentArea = null,
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA1,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA1
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA2_ID,
            polygon = Coordinates.Area2.map { it.toGeoPoint() },
            parentArea = Coordinates.Area1.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA2,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA2
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA3_ID,
            polygon = Coordinates.Area3.map { it.toGeoPoint() },
            parentArea = Coordinates.Area2.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA3,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA3
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA4_ID,
            polygon = Coordinates.Area4.map { it.toGeoPoint() },
            parentArea = Coordinates.Area3.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA4,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA4
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA5_ID,
            polygon = Coordinates.Area5.map { it.toGeoPoint() },
            parentArea = Coordinates.Area4.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA5,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA5
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA6_ID,
            polygon = Coordinates.Area6.map { it.toGeoPoint() },
            parentArea = Coordinates.Area5.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA6,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA6
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA7_ID,
            polygon = Coordinates.Area7.map { it.toGeoPoint() },
            parentArea = Coordinates.Area6.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA7,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA7
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA8_ID,
            polygon = Coordinates.Area8.map { it.toGeoPoint() },
            parentArea = Coordinates.Area7.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA8,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA8
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA9_ID,
            polygon = Coordinates.Area9.map { it.toGeoPoint() },
            parentArea = Coordinates.Area8.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA9,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA9
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA10_ID,
            polygon = Coordinates.Area10.map { it.toGeoPoint() },
            parentArea = Coordinates.Area9.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA10,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA10
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA11_ID,
            polygon = Coordinates.Area11.map { it.toGeoPoint() },
            parentArea = Coordinates.Area10.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA11,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA11
        ),
        DeliveryArea(
            id = DeliveryConstants.AREA12_ID,
            polygon = Coordinates.Area12.map { it.toGeoPoint() },
            parentArea = Coordinates.Area11.map { it.toGeoPoint() },
            deliveryPrice = DeliveryConstants.DELIVERY_PRICE_AREA12,
            freeDeliveryThreshold = DeliveryConstants.DELIVERY_PRICE_FREE_AREA12
        )
    )

    override fun getAllAreas(): List<DeliveryArea> = areas
}
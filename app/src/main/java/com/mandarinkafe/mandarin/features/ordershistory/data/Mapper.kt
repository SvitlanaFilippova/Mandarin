package com.mandarinkafe.mandarin.features.ordershistory.data

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.domain.models.getDetailsString
import com.mandarinkafe.mandarin.db.Saved_order
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

object Mapper {
    fun Saved_order.toSavedOrder() = SavedOrder(
        id = id,
        timestamp = timestamp,
        whenCreated = whenCreated,
        orderType = orderType,
        addressLine1 = addressLine1,
        addressDetails = addressDetails,
    )

    fun IncomingOrder.toSavedOrder() = SavedOrder(
        id = id,
        timestamp = timestamp,
        whenCreated = whenCreated ?: "",
        orderType = orderType?.name ?: "",
        addressLine1 = deliveryAddress?.streetAndBuilding ?: "",
        addressDetails = deliveryAddress?.getDetailsString() ?: "",
    )
}
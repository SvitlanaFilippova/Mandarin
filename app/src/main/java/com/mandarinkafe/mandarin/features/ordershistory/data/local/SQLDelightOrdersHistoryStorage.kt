package com.mandarinkafe.mandarin.features.ordershistory.data.local

import com.mandarinkafe.mandarin.db.SavedOrderQueries
import com.mandarinkafe.mandarin.features.ordershistory.data.Mapper.toSavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import javax.inject.Inject

class SQLDelightOrdersHistoryStorage @Inject constructor(private val queries: SavedOrderQueries) :
    OrdersHistoryStorage {
    override suspend fun getOrders(): List<SavedOrder> {
        return queries.selectAll()
            .executeAsList()
            .map { it.toSavedOrder() }
    }

    override suspend fun saveOrder(order: SavedOrder) {
        queries.insert(
            id = order.id,
            whenCreated = order.whenCreated,
            orderType = order.orderType,
            timestamp = order.timestamp,
            addressLine1 = order.addressLine1,
            addressDetails = order.addressDetails
        )
    }
}
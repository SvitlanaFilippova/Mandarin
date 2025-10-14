package com.mandarinkafe.mandarin.features.ordershistory.data.local

import com.mandarinkafe.mandarin.shared.database.SavedOrderQueries
import com.mandarinkafe.mandarin.features.ordershistory.data.Mapper.toSavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

class SQLDelightOrdersHistoryStorage(private val queries: SavedOrderQueries) :
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
            orderType = order.orderType?.name ?: "",
            timestamp = order.timestamp,
            addressLine1 = order.addressLine1,
            addressDetails = order.addressDetails,
            mealNames = order.mealNames,
            number = order.number
        )
    }

    override suspend fun removeOrderById(id: String) {
        queries.remove(id)
    }
}
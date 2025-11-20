package com.mandarinkafe.mandarin.features.ordershistory.data.impl

import com.mandarinkafe.mandarin.features.ordershistory.data.remote.OrdersHistoryRemoteDataSource
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

class OrdersHistoryRepositoryImpl(
    private val remoteDataSource: OrdersHistoryRemoteDataSource,
) : OrdersHistoryRepository {
    override suspend fun getOrders(): Resource<List<SavedOrder>> = remoteDataSource.getOrders()

    override suspend fun getOrderById(id: String) = remoteDataSource.getOrderById(id)

    override suspend fun saveOrder(order: SavedOrder) {
        remoteDataSource.saveOrder(order)
    }

    override suspend fun removeOrderById(id: String) {
        remoteDataSource.removeOrderById(id)
    }

    override suspend fun changePaymentMethod(orderId: String, paymentMethodCode: String): Resource<Unit> {
        return remoteDataSource.changePaymentMethod(orderId, paymentMethodCode)
    }
}
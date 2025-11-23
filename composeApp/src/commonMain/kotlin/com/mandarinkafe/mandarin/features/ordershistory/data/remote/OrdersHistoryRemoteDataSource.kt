package com.mandarinkafe.mandarin.features.ordershistory.data.remote

import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Resource

interface OrdersHistoryRemoteDataSource {
    suspend fun getOrders(): Resource<List<SavedOrder>>
    suspend fun getOrderById(id: String): SavedOrder?
    suspend fun saveOrder(order: SavedOrder)
    suspend fun removeOrderById(id: String)
    suspend fun changePaymentMethod(orderId: String, paymentMethodCode: String): Resource<Unit>
}


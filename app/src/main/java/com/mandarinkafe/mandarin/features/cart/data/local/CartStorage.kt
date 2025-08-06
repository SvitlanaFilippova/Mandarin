package com.mandarinkafe.mandarin.features.cart.data.local

import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import kotlinx.coroutines.flow.Flow

interface CartStorage {
    suspend fun clearCart()
    suspend fun addOrUpdateItem(item: StoredCartItem)
    suspend fun deleteItemById(id: String)
    fun observeCartItems(): Flow<List<StoredCartItem>>
}
package com.mandarinkafe.mandarin.features.cart.data.local

import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

interface CartStorage {
    suspend fun clearCart()
    suspend fun addOrUpdateItem(item: StoredCartItem)
    suspend fun deleteItemById(id: String)
    suspend fun getCartItems(): List<StoredCartItem>
    suspend fun getLastUpdated(): Long
    suspend fun updateLastUpdated(lastUpdated: Long)
}
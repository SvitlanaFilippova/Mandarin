package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem

interface CartWriter {
    suspend fun clear()
    suspend fun addOrUpdateItem(item: CartItem)
    suspend fun deleteItemById(id: String)
}
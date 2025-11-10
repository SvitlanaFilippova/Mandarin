package com.mandarinkafe.mandarin.features.cart.data.remote

import com.mandarinkafe.mandarin.features.cart.data.models.CartMetadata
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

interface CartRemoteDataSource {
    suspend fun getCart(): CartMetadata
    suspend fun syncCart(localCart: List<StoredCartItem>): CartMetadata
    suspend fun clearCart()
}


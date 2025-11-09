package com.mandarinkafe.mandarin.features.cart.data.remote

import com.mandarinkafe.mandarin.features.cart.data.models.CartMetadata
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

data class RemoteCart(
    val items: List<StoredCartItem>,
    val metadata: CartMetadata
)

interface CartRemoteDataSource {
    suspend fun getCart(): RemoteCart
    suspend fun syncCart(localCart: List<StoredCartItem>, metadata: CartMetadata)
    suspend fun clearCart()
}


package com.mandarinkafe.mandarin.features.cart.data.remote

import com.mandarinkafe.mandarin.features.cart.data.models.Cart
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

interface CartRemoteDataSource {
    suspend fun getCart(): Cart
    suspend fun syncCart(localCart: List<StoredCartItem>): Cart
    suspend fun clearCart()
}


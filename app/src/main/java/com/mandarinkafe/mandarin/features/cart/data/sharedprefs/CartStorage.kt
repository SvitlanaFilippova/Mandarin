package com.mandarinkafe.mandarin.features.cart.data.sharedprefs

import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

interface CartStorage {
    fun getCart(): List<StoredCartItem>
    fun clearCart()
    fun saveCart(items: List<StoredCartItem>)
}
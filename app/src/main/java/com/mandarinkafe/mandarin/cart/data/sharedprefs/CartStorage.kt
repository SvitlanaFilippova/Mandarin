package com.mandarinkafe.mandarin.cart.data.sharedprefs

import com.mandarinkafe.mandarin.cart.data.models.StoredCartItem

interface CartStorage {
    fun getCart(): List<StoredCartItem>
    fun clearCart()
    fun saveCart(items: List<StoredCartItem>)
}
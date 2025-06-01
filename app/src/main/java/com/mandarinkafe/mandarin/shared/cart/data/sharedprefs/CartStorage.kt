package com.mandarinkafe.mandarin.shared.cart.data.sharedprefs

import com.mandarinkafe.mandarin.shared.cart.data.models.StoredCartItem

interface CartStorage {
    fun getCart(): List<StoredCartItem>
    fun clearCart()
    fun saveCart(items: List<StoredCartItem>)
}
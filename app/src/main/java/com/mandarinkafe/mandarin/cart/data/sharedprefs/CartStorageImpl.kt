package com.mandarinkafe.mandarin.cart.data.sharedprefs

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mandarinkafe.mandarin.cart.data.models.StoredCartItem
import javax.inject.Inject

class CartStorageImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    CartStorage {

    override fun clearCart() {
        sharedPreferences.edit { remove(CART_KEY) }
        Log.d("DEBUG Cart", "CartStorageImpl - clearCart")
    }

    override fun saveCart(items: List<StoredCartItem>) {
        sharedPreferences.edit {
            putString(CART_KEY, Gson().toJson(items))
        }
        Log.d("DEBUG Cart", "CartStorageImpl - saveCart")
    }

    override fun getCart(): List<StoredCartItem> {
        return try {
            val json = sharedPreferences.getString(CART_KEY, null)
            val listType = object : TypeToken<List<StoredCartItem>>() {}.type
            if (json.isNullOrEmpty()) {
                mutableListOf()
            } else {
                Gson().fromJson(json, listType) ?: mutableListOf()
            }
        } catch (e: ClassCastException) {
            clearCart()
            mutableListOf()

        } catch (e: NullPointerException) {
            clearCart()
            mutableListOf()
        }
        Log.d("DEBUG Cart", "CartStorageImpl - getCart")
    }

    private companion object {
        const val CART_KEY = "CART_KEY"
    }
}
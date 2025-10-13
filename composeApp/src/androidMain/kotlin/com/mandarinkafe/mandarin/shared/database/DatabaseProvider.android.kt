package com.mandarinkafe.mandarin.shared.database

import android.content.Context
import com.mandarinkafe.mandarin.db.AppDatabase
import com.mandarinkafe.mandarin.db.CartItemsQueries

actual class DatabaseProvider(private val context: Context) {
    private val database = AppDatabase(context)
    
    actual val cartItemsQueries: CartItemsQueries
        get() = database.cartItemsQueries
}

package com.mandarinkafe.mandarin.shared.database

import com.mandarinkafe.mandarin.db.AppDatabase
import com.mandarinkafe.mandarin.db.CartItemsQueries
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseProvider {
    private val database = AppDatabase(NativeSqliteDriver())
    
    actual val cartItemsQueries: CartItemsQueries
        get() = database.cartItemsQueries
}

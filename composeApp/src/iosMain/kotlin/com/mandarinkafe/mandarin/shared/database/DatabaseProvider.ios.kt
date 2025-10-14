package com.mandarinkafe.mandarin.shared.database

import com.mandarinkafe.mandarin.shared.database.CartItemsQueries
import com.mandarinkafe.mandarin.shared.database.AppDatabase
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseProvider {
    private val driver = NativeSqliteDriver(AppDatabase.Schema, "app.db")
    private val database = AppDatabase(driver)

    actual val cartItemsQueries: CartItemsQueries
        get() = database.cartItemsQueries
}

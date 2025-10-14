package com.mandarinkafe.mandarin.shared.database

import android.content.Context
import com.mandarinkafe.mandarin.shared.database.CartItemsQueries
import com.mandarinkafe.mandarin.shared.database.AppDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseProvider(private val context: Context) {
    private val driver = AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
    private val database = AppDatabase(driver)

    actual val cartItemsQueries: CartItemsQueries
        get() = database.cartItemsQueries
}

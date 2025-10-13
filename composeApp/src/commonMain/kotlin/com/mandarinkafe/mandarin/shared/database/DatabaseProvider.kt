package com.mandarinkafe.mandarin.shared.database

import com.mandarinkafe.mandarin.db.CartItemsQueries

expect class DatabaseProvider {
    val cartItemsQueries: CartItemsQueries
}

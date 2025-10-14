package com.mandarinkafe.mandarin.shared.database

import com.mandarinkafe.mandarin.shared.database.CartItemsQueries

expect class DatabaseProvider {
    val cartItemsQueries: CartItemsQueries
}

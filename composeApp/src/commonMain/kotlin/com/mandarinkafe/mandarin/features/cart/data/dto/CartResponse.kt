package com.mandarinkafe.mandarin.features.cart.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val items: List<CartItemDto> = emptyList(),
    @SerialName("last_updated")
    val lastUpdated: Long = 0L, // время последнего изменения всей корзины (задаётся на сервере)
) : Response() {
    init {
        resultCode = HTTP_SUCCESS
    }
}


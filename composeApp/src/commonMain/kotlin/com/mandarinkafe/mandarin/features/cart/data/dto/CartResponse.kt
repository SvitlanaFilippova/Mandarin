package com.mandarinkafe.mandarin.features.cart.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val items: List<CartItemDto> = emptyList(),
    @SerialName("updated_at")
    val updatedAt: Long = 0L, // Время последнего изменения корзины
    @SerialName("is_deleted")
    val isDeleted: Boolean = false, // Флаг удаления корзины
) : Response() {
    init {
        resultCode = HTTP_SUCCESS
    }
}


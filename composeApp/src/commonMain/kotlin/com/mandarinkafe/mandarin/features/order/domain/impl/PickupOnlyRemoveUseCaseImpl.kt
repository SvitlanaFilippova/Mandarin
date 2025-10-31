package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.order.domain.api.PickupOnlyRemoveUseCase

class PickupOnlyRemoveUseCaseImpl(private val cartWriter: CartWriter) : PickupOnlyRemoveUseCase {
    override suspend fun invoke(itemIds: List<String>) {
        itemIds.forEach { cartWriter.deleteItemById(it) }
    }
}
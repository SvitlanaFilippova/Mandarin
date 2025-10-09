package com.mandarinkafe.mandarin.features.order.domain.api

interface PickupOnlyRemoveUseCase {
    suspend operator fun invoke(itemIds: List<String>)
}
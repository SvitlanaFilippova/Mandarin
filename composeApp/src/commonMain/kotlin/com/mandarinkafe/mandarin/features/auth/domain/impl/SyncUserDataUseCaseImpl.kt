package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository

class SyncUserDataUseCaseImpl(
    private val favoritesRepository: FavoritesWriter,
    private val addressesRepository: SavedAddressRepository,
    private val ordersRepository: OrdersHistoryRepository,
    ) : SyncUserDataUseCase {
    override suspend fun invoke() {
        favoritesRepository.sync()
        addressesRepository.sync()
        ordersRepository.sync()
    }
}
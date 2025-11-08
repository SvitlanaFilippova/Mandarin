package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase

class SyncUserDataUseCaseImpl(
    private val favoritesRepository: FavoritesWriter,
) : SyncUserDataUseCase {
    override suspend fun invoke() {
        favoritesRepository.sync()
    }
}
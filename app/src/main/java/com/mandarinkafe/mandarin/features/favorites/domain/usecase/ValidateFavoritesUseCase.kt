package com.mandarinkafe.mandarin.features.favorites.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.util.Resource

interface ValidateFavoritesUseCase {
    suspend operator fun invoke(rawRecords: Set<FavoriteRecord>): Resource<List<CustomizedMeal>>
}
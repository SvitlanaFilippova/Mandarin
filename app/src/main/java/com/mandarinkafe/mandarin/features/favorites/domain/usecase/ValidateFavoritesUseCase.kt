package com.mandarinkafe.mandarin.features.favorites.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord

interface ValidateFavoritesUseCase {
    suspend operator fun invoke(rawRecords: Set<FavoriteRecord>): List<CustomizedMeal>
}
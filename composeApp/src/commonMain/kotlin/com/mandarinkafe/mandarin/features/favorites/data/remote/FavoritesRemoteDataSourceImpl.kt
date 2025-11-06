package com.mandarinkafe.mandarin.features.favorites.data.remote

import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.network.FavoritesServerApi

class FavoritesRemoteDataSourceImpl(
    private val api: FavoritesServerApi,
    private val authRepository: AuthRepository,
) : FavoritesRemoteDataSource {

    override suspend fun getFavorites(): Set<StoredFavoriteMeal>  {
        val token = authRepository.getAccessToken() ?: return emptySet()
        return try {
            api.getFavorites("Bearer $token").data.map { it.toDomain() }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    override suspend fun syncFavorites(localFavorites: Set<FavoriteRecord>) {
        val token = authRepository.getAccessToken() ?: return
        try {
            val body = localFavorites.map { it.toDto() }
            api.updateFavorites("Bearer $token", body)
        } catch (_: Exception) {}
    }
}
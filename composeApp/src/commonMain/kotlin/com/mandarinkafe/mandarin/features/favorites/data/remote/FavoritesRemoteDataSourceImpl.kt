package com.mandarinkafe.mandarin.features.favorites.data.remote

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toDto
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStored
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.network.FavoritesServerApi
import com.mandarinkafe.mandarin.features.favorites.data.network.RemoteFavoritesUpdateRequest

class FavoritesRemoteDataSourceImpl(
    private val api: FavoritesServerApi,
    private val authRepository: AuthRepository,
    private val menuCache: MenuCache,
) : FavoritesRemoteDataSource {

    override suspend fun getFavorites(): Set<StoredFavoriteMeal> {
        val token = authRepository.getAccessToken() ?: return emptySet()
        return try {
            val response = api.getFavorites("Bearer $token")
            response.favorites?.map { favoriteDto ->
                favoriteDto.toStored(menuCache)
            }?.toSet() ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    override suspend fun syncFavorites(localFavorites: Set<FavoriteRecord>) {
        val token = authRepository.getAccessToken() ?: return
        try {
            // Преобразуем FavoriteRecord -> StoredFavoriteMeal -> FavoriteDto
            val favoriteDtos = localFavorites.map { record ->
                val stored = record.toStored()
                stored.toDto()
            }.toList()

            val request = RemoteFavoritesUpdateRequest(favorites = favoriteDtos)
            api.updateFavorites("Bearer $token", request)
        } catch (_: Exception) {}
    }
}
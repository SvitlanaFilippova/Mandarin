package com.mandarinkafe.mandarin.features.favorites.data.remote

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toDto
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toStored
import com.mandarinkafe.mandarin.features.favorites.data.models.Favorites
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.network.FavoritesServerApi
import com.mandarinkafe.mandarin.features.favorites.data.network.RemoteFavoritesUpdateRequest
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier

class FavoritesRemoteDataSourceImpl(
    private val api: FavoritesServerApi,
    private val authRepository: AuthRepository,
    private val menuCache: MenuCache,
) : FavoritesRemoteDataSource {

    override suspend fun getFavorites(): Favorites {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return Favorites(emptySet(), 0L)
        }
        return try {
            val response = api.getFavorites("$BEARER_TOKEN_TYPE $token")
            
            // Проверяем resultCode ответа
            if (response.resultCode != HTTP_SUCCESS) {
                return Favorites(emptySet(), 0L)
            }
            
            val items = response.favorites?.mapNotNull { favoriteDto ->
                favoriteDto.toStored(menuCache)
            }?.toSet() ?: emptySet()
            Favorites(items, response.lastUpdated)
        } catch (e: Exception) {
            Napier.e("Ошибка при получении избранного с сервера", e)
            Favorites(emptySet(), 0L)
        }
    }

    override suspend fun syncFavorites(localFavorites: Set<StoredFavoriteMeal>): Favorites {
        val token = authRepository.getAccessToken()
        if (token == null) {
            return Favorites(emptySet(), 0L)
        }
        return try {
            // Отправляем избранное с реальными updatedAt:
            // - updatedAt = 0L → маркер "эта запись изменена/новая, обновляй"
            // - updatedAt > 0 → реальное значение для мержа на сервере
            val favoriteDtos = localFavorites.map { item ->
                item.toDto() // Отправляем как есть, включая реальные updatedAt
            }.toList()

            val request = RemoteFavoritesUpdateRequest(favorites = favoriteDtos, lastUpdated = 0L)
            val response = api.updateFavorites("$BEARER_TOKEN_TYPE $token", request)

            // Проверяем, была ли ошибка на сервере
            if (response.resultCode != HTTP_SUCCESS) {
                // При ошибке возвращаем локальное избранное, чтобы не потерять данные
                return Favorites(localFavorites, 0L)
            }

            // Получаем обновлённое избранное с updatedAt от сервера
            val items = response.favorites?.mapNotNull { favoriteDto ->
                favoriteDto.toStored(menuCache)
            }?.toSet() ?: emptySet()
            Favorites(items, response.lastUpdated)
        } catch (e: Exception) {
            Napier.e("Ошибка при отправке избранного на сервер", e)
            Favorites(emptySet(), 0L)
        }
    }
}
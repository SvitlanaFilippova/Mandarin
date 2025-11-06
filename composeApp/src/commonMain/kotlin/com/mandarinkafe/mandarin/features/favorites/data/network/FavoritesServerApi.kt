package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import io.ktor.client.HttpClient

class FavoritesServerApi(private val client: HttpClient) {
    suspend fun getFavorites(token: String): Response {
        TODO()
    }

    suspend fun updateFavorites(token: String, body: RemoteFavoritesUpdateRequest) {
        TODO()
    }
}
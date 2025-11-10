package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteFavoritesUpdateRequest(
    val favorites: List<FavoriteDto>,
    @SerialName("last_updated")
    val lastUpdated: Long = 0L
)

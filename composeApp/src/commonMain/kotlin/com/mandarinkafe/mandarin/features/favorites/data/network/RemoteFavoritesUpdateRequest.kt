package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto
import kotlinx.serialization.Serializable

@Serializable
data class RemoteFavoritesUpdateRequest(val data: List<FavoriteDto>)

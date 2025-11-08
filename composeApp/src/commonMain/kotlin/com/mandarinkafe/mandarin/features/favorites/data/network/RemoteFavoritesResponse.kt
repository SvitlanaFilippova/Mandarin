package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto
import kotlinx.serialization.Serializable

@Serializable
data class RemoteFavoritesResponse(val data: Set<FavoriteDto>? = null) : Response()
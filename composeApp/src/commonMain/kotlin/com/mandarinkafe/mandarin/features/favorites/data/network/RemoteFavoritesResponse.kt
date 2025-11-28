package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteFavoritesResponse(
    val favorites: Set<FavoriteDto>? = null,
    @SerialName("last_updated")
    val lastUpdated: Long = 0L, // время последнего изменения всего избранного (задаётся на сервере)
) : Response()
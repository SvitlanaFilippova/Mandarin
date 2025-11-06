package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto

data class RemoteFavoritesUpdateRequest(val data: Set<FavoriteDto> )

package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto

class RemoteFavoritesResponse(val data: Set<FavoriteDto>? = null) : Response()
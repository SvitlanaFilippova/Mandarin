package com.mandarinkafe.mandarin.core.data.api

interface FavoritesReader {
    suspend fun getBaseFavoritesIds(): Set<String>
}
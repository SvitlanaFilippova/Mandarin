package com.mandarinkafe.mandarin.core.data.api

interface FavoritesReader {
    suspend fun getFavoritesIds(): Set<String>
}
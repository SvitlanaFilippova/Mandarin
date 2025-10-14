package com.mandarinkafe.mandarin.features.favorites.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.models.isBase
import com.mandarinkafe.mandarin.features.favorites.data.models.sameAs
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoritesStorageImpl(
    private val dataStore: DataStore<Preferences>
) : FavoritesStorage {

    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun toggleFavorite(meal: StoredFavoriteMeal): Boolean {
        val currentSetResult = getFavorites()
        if (currentSetResult is FavoritesStorageResult.Success) {
            val currentSet = currentSetResult.favorites.toMutableSet()
            val isBase = meal.isBase()
            val alreadyExists = if (isBase) {
                currentSet.any { it.mealId == meal.mealId && it.isBase() }
            } else {
                currentSet.any { it.sameAs(meal) }
            }

            val isNowFavorite = if (alreadyExists) {
                if (isBase) {
                    currentSet.removeAll { it.mealId == meal.mealId && it.isBase() }
                } else {
                    currentSet.removeAll { it.sameAs(meal) }
                }
                false
            } else {
                currentSet.add(meal)
                true
            }

            saveFavorites(currentSet)
            return isNowFavorite
        } else {
            return false
        }
    }

    override suspend fun saveFavorites(updatedFavorites: Set<StoredFavoriteMeal>) {
        val jsonString = json.encodeToString<Set<StoredFavoriteMeal>>(updatedFavorites)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(FAVORITES_KEY)] = jsonString
        }
    }

    override suspend fun getFavorites(): FavoritesStorageResult =
        dataStore.data
            .map { prefs ->
                val jsonString = prefs[stringPreferencesKey(FAVORITES_KEY)]
                if (jsonString == null) {
                    FavoritesStorageResult.Success(emptySet())
                } else {
                    runCatching {
                        val result = json.decodeFromString<Set<StoredFavoriteMeal>>(jsonString)
                        FavoritesStorageResult.Success(result)
                    }.getOrElse { e ->
                        Napier.e("Ошибка чтения избранного", e)
                        FavoritesStorageResult.Corrupted()
                    }
                }
            }
            .first()

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }
}


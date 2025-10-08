package com.mandarinkafe.mandarin.features.favorites.data.sharedprefs

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.models.isBase
import com.mandarinkafe.mandarin.features.favorites.data.models.sameAs
import com.mandarinkafe.mandarin.util.AppLog

class FavoritesStorageImpl(
    private val sharedPreferences: SharedPreferences
) : FavoritesStorage {
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

    override fun saveFavorites(favorites: Set<StoredFavoriteMeal>) {
        val json = Gson().toJson(favorites)
        sharedPreferences.edit {
            putString(FAVORITES_KEY, json)
        }
    }

    @SuppressLint("LogNotTimber")
    override suspend fun getFavorites(): FavoritesStorageResult {
        val json = sharedPreferences.getString(FAVORITES_KEY, null)
        return try {
            if (json == null) {
                AppLog.w("Favorites key is null, returning emptySet()")
                FavoritesStorageResult.Success(emptySet())
            } else {
                val listType = object : TypeToken<Set<StoredFavoriteMeal>>() {}.type
                val result: Set<StoredFavoriteMeal> = Gson().fromJson(json, listType) ?: emptySet()
                FavoritesStorageResult.Success(result)
            }
        } catch (e: Exception) {
            AppLog.e("Ошибка чтения избранного: ${e.message}, json=$json", e)
            sharedPreferences.edit { remove(FAVORITES_KEY) }
            FavoritesStorageResult.Corrupted()
        }
    }

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }
}
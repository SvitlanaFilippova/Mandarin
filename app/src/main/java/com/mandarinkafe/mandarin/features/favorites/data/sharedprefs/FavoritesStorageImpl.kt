package com.mandarinkafe.mandarin.features.favorites.data.sharedprefs

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.models.isBase
import com.mandarinkafe.mandarin.features.favorites.data.models.sameAs
import javax.inject.Inject

class FavoritesStorageImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : FavoritesStorage {
    private val logTag = "FavoritesStorage"
    override suspend fun toggleFavorite(meal: StoredFavoriteMeal): Boolean {
        Log.d(logTag, "toggleFavorite called with meal=$meal")
        val currentSetResult = getFavorites()
        if (currentSetResult is FavoritesStorageResult.Success) {
            val currentSet = currentSetResult.favorites.toMutableSet()
            Log.d(logTag, "Current favorites before toggle: $currentSet")

            val isBase = meal.isBase()
            val alreadyExists = if (isBase) {
                currentSet.any { it.mealId == meal.mealId && it.isBase() }
            } else {
                currentSet.any { it.sameAs(meal) }
            }
            Log.d(logTag, "isBase=$isBase, alreadyExists=$alreadyExists")

            val isNowFavorite = if (alreadyExists) {
                if (isBase) {
                    currentSet.removeAll { it.mealId == meal.mealId && it.isBase() }
                } else {
                    currentSet.removeAll { it.sameAs(meal) }
                }
                Log.d(logTag, "Removed meal=$meal")
                false
            } else {
                currentSet.add(meal)
                Log.d(logTag, "Added meal=$meal")
                true
            }

            saveFavorites(currentSet)
            Log.d(
                logTag,
                "Favorites after toggle: $currentSet, isNowFavorite=$isNowFavorite"
            )
            return isNowFavorite
        } else {
            return false
        }
    }

    override fun saveFavorites(favorites: Set<StoredFavoriteMeal>) {
        val json = Gson().toJson(favorites)
        Log.d(logTag, "Saving favorites JSON=$json")
        sharedPreferences.edit {
            putString(FAVORITES_KEY, json)
        }
    }

    override suspend fun getFavorites(): FavoritesStorageResult {
        val json = sharedPreferences.getString(FAVORITES_KEY, null)
        Log.d(logTag, "Reading favorites raw JSON=$json")

        return try {
            if (json == null) {
                Log.w(logTag, "Favorites key is null, returning emptySet()")
                FavoritesStorageResult.Success(emptySet())
            } else {
                val listType = object : TypeToken<Set<StoredFavoriteMeal>>() {}.type
                val result: Set<StoredFavoriteMeal> = Gson().fromJson(json, listType) ?: emptySet()
                Log.d(logTag, "Parsed favorites=$result")
                FavoritesStorageResult.Success(result)
            }
        } catch (e: Exception) {
            Log.e(logTag, "Ошибка чтения избранного: ${e.message}, json=$json", e)
            sharedPreferences.edit { remove(FAVORITES_KEY) }
            FavoritesStorageResult.Corrupted()
        }
    }

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }
}
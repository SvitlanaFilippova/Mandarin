package com.mandarinkafe.mandarin.favorites.data.sharedprefs

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mandarinkafe.mandarin.favorites.domain.models.FavoriteMeal
import javax.inject.Inject

class LocalStorageImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    LocalStorage {

    override fun addToFavorites(meal: FavoriteMeal) {
        val updatedFavorites = getFavorites().toMutableSet().apply { add(meal) }
        sharedPreferences.edit { putString(FAVORITES_KEY, Gson().toJson(updatedFavorites)) }
        Log.d("DEBUG LocalStorage", "Added to Favorites: $meal")
    }

    override fun removeFromFavorites(mealId: String) {
        val updatedFavorites = getFavorites().filter { it.id != mealId }
        sharedPreferences.edit {
            putString(FAVORITES_KEY, Gson().toJson(updatedFavorites))
        }
        Log.d("DEBUG LocalStorage", "Removed from Favorites: $mealId")
    }

    override fun getFavorites(): Set<FavoriteMeal> {
        return try {
            val json = sharedPreferences.getString(FAVORITES_KEY, null)
            val listType = object : TypeToken<Set<FavoriteMeal>>() {}.type
            if (json.isNullOrEmpty()) {
                mutableSetOf()
            } else {
                Gson().fromJson(json, listType) ?: mutableSetOf()
            }
        } catch (e: ClassCastException) {
            sharedPreferences.edit().remove(FAVORITES_KEY).apply()
            mutableSetOf()
        }
    }

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }
}
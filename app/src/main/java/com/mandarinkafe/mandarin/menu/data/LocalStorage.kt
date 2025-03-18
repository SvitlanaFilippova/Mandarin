package com.mandarinkafe.mandarin.menu.data

import android.content.SharedPreferences
import android.util.Log

class LocalStorage(private val sharedPreferences: SharedPreferences) {
    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }

    fun addToFavorites(mealId: String) {
        changeFavorites(mealId = mealId, remove = false)
        Log.d("DEBUG LocalStorage", "Added to Favorites: $mealId")
    }

    fun removeFromFavorites(mealId: String) {
        changeFavorites(mealId = mealId, remove = true)
        Log.d("DEBUG LocalStorage", "Removed from fvorites: $mealId")
    }

    fun getSavedFavorites(): Set<String> {
        val savedIds = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        for (s in savedIds) {
            Log.d("DEBUG LocalStorage", "savedFavoritesID: $s")
        }

        return savedIds
    }

    private fun changeFavorites(mealId: String, remove: Boolean) {
        val mutableSet = getSavedFavorites().toMutableSet()
        val modified = if (remove) mutableSet.remove(mealId) else mutableSet.add(mealId)
        if (modified) sharedPreferences.edit().putStringSet(FAVORITES_KEY, mutableSet).apply()
    }
}
package com.mandarinkafe.mandarin.favorites.data

import android.content.SharedPreferences
import javax.inject.Inject

class LocalStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun addToFavorites(mealId: String) {
        changeFavorites(mealId = mealId, remove = false)
    }

    fun removeFromFavorites(mealId: String) {
    }

    fun getSavedFavorites(): Set<String> {
        val savedIds = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        return savedIds
    }

    private fun changeFavorites(mealId: String, remove: Boolean) {
        val mutableSet = getSavedFavorites().toMutableSet()
        val modified = if (remove) mutableSet.remove(mealId) else mutableSet.add(mealId)
        if (modified) sharedPreferences.edit().putStringSet(FAVORITES_KEY, mutableSet).apply()
    }

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }
}
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

class FavoritesStorageImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    FavoritesStorage {

    override suspend fun toggleFavorite(meal: StoredFavoriteMeal): Boolean {
        // Получаем текущее множество избранного
        val currentSet = getFavorites().toMutableSet()

        // Определяем, базовая это запись или кастомная
        val isBase = meal.isBase()

        // Проверяем, есть ли уже запись в текущем множестве
        val alreadyExists = if (isBase) {
            // для базового — ищем именно Base по mealId
            currentSet.any { it.mealId == meal.mealId && it.isBase() }
        } else {
            // для кастомного — полное совпадение всех полей, кроме timestamp
            currentSet.any { it.sameAs(meal) }
        }

        val isNowFavorite = if (alreadyExists) {
            // Если уже есть — удаляем
            if (isBase) {
                currentSet.removeAll { it.mealId == meal.mealId && it.isBase() }
            } else {
                currentSet.removeAll { it.sameAs(meal) }
            }
            false
        } else {
            // Если нет — добавляем
            currentSet.add(meal)
            true
        }

        // Сохраняем обновлённый сет
        saveFavorites(currentSet)
        return isNowFavorite
    }

    override fun saveFavorites(favorites: Set<StoredFavoriteMeal>) {
        sharedPreferences.edit {
            putString(FAVORITES_KEY, Gson().toJson(favorites))
        }
    }

    override suspend fun getFavorites(): Set<StoredFavoriteMeal> {
        val json = sharedPreferences.getString(FAVORITES_KEY, null)
        return try {
            val listType = object : TypeToken<Set<StoredFavoriteMeal>>() {}.type
            Gson().fromJson(json, listType) ?: emptySet()

        } catch (e: Exception) {
            Log.e("FavoritesStorage", "Ошибка чтения избранного: ${e.message}")
            sharedPreferences.edit { remove(FAVORITES_KEY) }
            emptySet()
        }
    }

    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }
}
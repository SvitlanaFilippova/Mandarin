package com.mandarinkafe.mandarin.shared.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Базовый класс для работы с DataStore с JSON сериализацией
 * Аналогичен работе с SharedPreferences, но использует DataStore
 */
abstract class JsonDataStoreStorage(
    protected val dataStore: DataStore<Preferences>
) {
    protected val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Сохраняет объект в виде JSON строки
     */
    protected suspend fun <T> saveObject(key: String, value: T, serializer: KSerializer<T>) {
        val jsonString = json.encodeToString(serializer, value)
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = jsonString
        }
    }
    
    /**
     * Читает объект из JSON строки
     */
    protected suspend fun <T> getObject(key: String, serializer: KSerializer<T>): T? {
        return try {
            val jsonString = dataStore.data.map { preferences ->
                preferences[stringPreferencesKey(key)]
            }.first()
            
            if (jsonString.isNullOrEmpty()) {
                null
            } else {
                json.decodeFromString(serializer, jsonString)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Читает объект из JSON строки с обработкой ошибок
     */
    protected suspend fun <T> getObjectWithErrorHandling(
        key: String, 
        serializer: KSerializer<T>,
        onError: () -> Unit = {}
    ): T? {
        return try {
            val jsonString = dataStore.data.map { preferences ->
                preferences[stringPreferencesKey(key)]
            }.first()
            
            if (jsonString.isNullOrEmpty()) {
                null
            } else {
                json.decodeFromString(serializer, jsonString)
            }
        } catch (e: Exception) {
            onError()
            null
        }
    }
    
    /**
     * Получает Flow для наблюдения за изменениями объекта
     */
    protected fun <T> getObjectFlow(key: String, serializer: KSerializer<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[stringPreferencesKey(key)]
            if (jsonString.isNullOrEmpty()) {
                null
            } else {
                try {
                    json.decodeFromString(serializer, jsonString)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    /**
     * Удаляет значение по ключу
     */
    protected suspend fun remove(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
    
    /**
     * Очищает все данные
     */
    protected suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}


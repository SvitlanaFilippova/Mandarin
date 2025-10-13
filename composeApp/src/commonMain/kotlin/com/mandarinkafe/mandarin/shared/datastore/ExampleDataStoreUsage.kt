package com.mandarinkafe.mandarin.shared.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

/**
 * Пример использования JsonDataStoreStorage
 */
class ExampleDataStoreUsage(
    dataStore: DataStore<Preferences>
) : JsonDataStoreStorage(dataStore) {
    
    private companion object {
        const val USER_NAME_KEY = "USER_NAME_KEY"
        const val USER_AGE_KEY = "USER_AGE_KEY"
    }
    
    /**
     * Сохраняет имя пользователя
     */
    suspend fun saveUserName(name: String) {
        saveObject(USER_NAME_KEY, name, String.serializer())
    }
    
    /**
     * Получает имя пользователя
     */
    suspend fun getUserName(): String? {
        return getObject(USER_NAME_KEY, String.serializer())
    }
    
    /**
     * Сохраняет возраст пользователя
     */
    suspend fun saveUserAge(age: Int) {
        saveObject(USER_AGE_KEY, age, Int.serializer())
    }
    
    /**
     * Получает возраст пользователя
     */
    suspend fun getUserAge(): Int? {
        return getObject(USER_AGE_KEY, Int.serializer())
    }
    
    /**
     * Получает Flow для наблюдения за изменениями имени
     */
    fun getUserNameFlow() = getObjectFlow(USER_NAME_KEY, String.serializer())
    
    /**
     * Очищает данные пользователя
     */
    suspend fun clearUserData() {
        remove(USER_NAME_KEY)
        remove(USER_AGE_KEY)
    }
}

/**
 * Пример сериализуемого класса
 */
@Serializable
data class UserProfile(
    val name: String,
    val age: Int,
    val email: String?
)


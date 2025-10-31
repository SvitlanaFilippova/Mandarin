package com.mandarinkafe.mandarin.features.order.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserInfoStorageImpl(
    private val dataStore: DataStore<Preferences>,
) : UserInfoStorage {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getUserInfo(): UserInfo? =
        dataStore.data
            .map { prefs ->
                val jsonString = prefs[stringPreferencesKey(USER_INFO_KEY)]
                if (jsonString.isNullOrEmpty()) {
                    null
                } else {
                    runCatching {
                        json.decodeFromString<UserInfo>(jsonString)
                    }.getOrElse { e ->
                        Napier.e("Ошибка чтения UserInfo", e)
                        null
                    }
                }
            }
            .first()

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        val jsonString = json.encodeToString<UserInfo>(userInfo)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(USER_INFO_KEY)] = jsonString
        }
    }

    override suspend fun clearUserInfo() {
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(USER_INFO_KEY))
        }
    }

    private companion object {
        const val USER_INFO_KEY = "USER_INFO_KEY"
    }
}


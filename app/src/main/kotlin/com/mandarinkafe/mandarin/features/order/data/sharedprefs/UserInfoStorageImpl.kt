package com.mandarinkafe.mandarin.features.order.data.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

class UserInfoStorageImpl(
    private val sharedPreferences: SharedPreferences
) : UserInfoStorage {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getUserInfo(): UserInfo? {
        return try {
            val jsonString = sharedPreferences.getString(USER_INFO_KEY, null)
            if (jsonString.isNullOrEmpty()) {
                null
            } else {
                json.decodeFromString<UserInfo>(jsonString)
            }
        } catch (e: Exception) {
            Napier.e("Ошибка чтения UserInfo: ${e.message}")
            clearUserInfo()
            null
        }
    }

    override fun saveUserInfo(userInfo: UserInfo) {
        val jsonString = json.encodeToString(userInfo)
        sharedPreferences.edit { putString(USER_INFO_KEY, jsonString) }
    }

    override fun clearUserInfo() {
        sharedPreferences.edit { remove(USER_INFO_KEY) }
    }

    private companion object {
        const val USER_INFO_KEY = "USER_INFO_KEY"
    }
}
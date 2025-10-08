package com.mandarinkafe.mandarin.features.order.data.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.util.AppLog

class UserInfoStorageImpl(
    private val sharedPreferences: SharedPreferences
) : UserInfoStorage {

    private val gson = Gson()

    override fun getUserInfo(): UserInfo? {
        return try {
            val json = sharedPreferences.getString(USER_INFO_KEY, null)
            if (json.isNullOrEmpty()) {
                null
            } else {
                gson.fromJson(json, UserInfo::class.java)
            }
        } catch (e: Exception) {
            AppLog.e(
                "Ошибка чтения UserInfo: ${e.message}. Очищаю сохранённые данные"
            )
            clearUserInfo()
            null
        }
    }

    override fun saveUserInfo(userInfo: UserInfo) {
        val json = gson.toJson(userInfo)
        sharedPreferences.edit { putString(USER_INFO_KEY, json) }
    }

    override fun clearUserInfo() {
        sharedPreferences.edit { remove(USER_INFO_KEY) }
    }

    private companion object {
        const val USER_INFO_KEY = "USER_INFO_KEY"
    }
}
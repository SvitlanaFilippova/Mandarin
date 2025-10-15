package com.mandarinkafe.mandarin.features.order.data.datastore

import com.mandarinkafe.mandarin.core.domain.models.UserInfo

interface UserInfoStorage {
    suspend fun getUserInfo(): UserInfo?
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun clearUserInfo()
}






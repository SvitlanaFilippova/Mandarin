package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.UserInfo

interface UserInfoRepository {
    suspend fun getUserInfo(): UserInfo?
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun clearUserInfo()
}
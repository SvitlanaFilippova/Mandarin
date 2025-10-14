package com.mandarinkafe.mandarin.features.order.data.impl

import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.features.order.data.datastore.UserInfoStorage
import com.mandarinkafe.mandarin.features.order.domain.api.UserInfoRepository

class UserInfoRepositoryImpl(private val userInfoStorage: UserInfoStorage) : UserInfoRepository {

    override suspend fun getUserInfo(): UserInfo? {
        return userInfoStorage.getUserInfo()
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        userInfoStorage.saveUserInfo(userInfo)
    }

    override suspend fun clearUserInfo() {
        userInfoStorage.clearUserInfo()
    }
}


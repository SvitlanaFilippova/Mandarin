package com.mandarinkafe.mandarin.features.order.data.impl

import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.features.order.data.sharedprefs.UserInfoStorage
import com.mandarinkafe.mandarin.features.order.domain.api.UserInfoRepository

class UserInfoRepositoryImpl(private val userInfoStorage: UserInfoStorage) : UserInfoRepository {

    override fun getUserInfo(): UserInfo? {
        return userInfoStorage.getUserInfo()
    }

    override fun saveUserInfo(userInfo: UserInfo) {
        userInfoStorage.saveUserInfo(userInfo)
    }

    override fun clearUserInfo() {
        userInfoStorage.clearUserInfo()
    }
}
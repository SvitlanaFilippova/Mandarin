package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.UserInfo

interface UserInfoRepository {
    fun getUserInfo(): UserInfo?
    fun saveUserInfo(userInfo: UserInfo)
    fun clearUserInfo()
}
package com.mandarinkafe.mandarin.features.order.data.sharedprefs

import com.mandarinkafe.mandarin.core.domain.models.UserInfo

interface UserInfoStorage {
    fun getUserInfo(): UserInfo?
    fun saveUserInfo(userInfo: UserInfo)
    fun clearUserInfo()
}
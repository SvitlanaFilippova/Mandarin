package com.mandarinkafe.mandarin.features.account.domain.api

import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.features.auth.data.dto.UserInfoDto
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface UserInfoRepository {
    val userInfo: StateFlow<UserInfo?>

    fun getUserInfo(): UserInfo?
    fun updateFromServer(userInfoDto: UserInfoDto)
    suspend fun updateName(accessToken: String, name: String): Resource<Unit>
    fun clearUserInfo()
}


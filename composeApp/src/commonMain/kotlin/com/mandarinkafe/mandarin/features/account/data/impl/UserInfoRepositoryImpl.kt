package com.mandarinkafe.mandarin.features.account.data.impl

import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.auth.data.dto.UpdateNameRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.UserInfoDto
import com.mandarinkafe.mandarin.features.auth.data.network.AuthNetworkClient
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserInfoRepositoryImpl(
    private val networkClient: AuthNetworkClient,
) : UserInfoRepository {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    override val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    override fun getUserInfo(): UserInfo? {
        return _userInfo.value
    }

    override fun updateFromServer(userInfoDto: UserInfoDto) {
        val name = userInfoDto.name.orEmpty()
        val phone = userInfoDto.phone.orEmpty()

        _userInfo.value = UserInfo(
            name = name,
            phone = phone
        )

        Napier.d("UserInfoRepository: Updated from server - name: $name, phone: $phone")
    }

    override suspend fun updateName(accessToken: String, name: String): Resource<Unit> {
        return try {
            val response = networkClient.updateUserName(
                accessToken = accessToken,
                request = UpdateNameRequest(name = name)
            )

            when (response.resultCode) {
                HTTP_SUCCESS -> {
                    // Обновляем локальный кэш
                    _userInfo.value = _userInfo.value?.copy(name = name)
                    Napier.d("UserInfoRepository: Name updated successfully")
                    Resource.Success(Unit)
                }

                NO_CONNECTION -> {
                    Napier.w("UserInfoRepository: updateName - No connection")
                    Resource.ErrorNoInternet()
                }

                else -> {
                    Napier.e("UserInfoRepository: updateName - Error ${response.resultCode}")
                    Resource.ErrorOther("Ошибка обновления имени")
                }
            }
        } catch (e: Exception) {
            Napier.e("UserInfoRepository: updateName - Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override fun clearUserInfo() {
        _userInfo.value = null
        Napier.d("UserInfoRepository: User info cleared")
    }
}


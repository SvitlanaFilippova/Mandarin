package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusResponse
import com.mandarinkafe.mandarin.features.auth.data.toDomain
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class AuthRepositoryImpl(
    private val networkClient: ServerNetworkClient,
) : AuthRepository {

    override suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Resource<PhoneVerificationData> {
        Napier.d("AUTH DEBUG: AuthRepository.requestPhoneVerification() called with phone: ${request.phone}")
        return try {
            val response = networkClient.requestPhoneVerification(request)
            Napier.d("AUTH DEBUG: NetworkClient returned response with resultCode: ${response.resultCode}")
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.d("AUTH DEBUG: Returning ErrorNoInternet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationResponse
                    Napier.d("AUTH DEBUG: HTTP_SUCCESS, data: ${wrapper.data}")
                    wrapper.data?.let {
                        val domainData = it.toDomain()
                        Napier.d("AUTH DEBUG: Converted to domain model: checkId=${domainData.checkId}, callPhone=${domainData.phoneToCall}")
                        Resource.Success(domainData)
                    } ?: run {
                        Napier.d("AUTH DEBUG: Warning: wrapper.data is null, returning ErrorOther")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (HTTP_SERVER_ERROR)")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (unknown error code: ${response.resultCode})")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.d("AUTH DEBUG: Exception in requestPhoneVerification: ${e.message}")
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun checkVerificationStatus(request: PhoneVerificationStatusRequest): Resource<PhoneVerificationStatus> {
        Napier.d("AUTH DEBUG: AuthRepository.checkVerificationStatus() called with phone: ${request.phone}")
        return try {
            val response = networkClient.checkVerificationStatus(request)
            Napier.d("AUTH DEBUG: NetworkClient returned response with resultCode: ${response.resultCode}")
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.d("AUTH DEBUG: Returning ErrorNoInternet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationStatusResponse
                    Napier.d("AUTH DEBUG: HTTP_SUCCESS, data: ${wrapper.data}")
                    wrapper.data?.let {
                        val domainStatus = it.toDomain()
                        Napier.d("AUTH DEBUG: Converted to domain model: isVerified=${domainStatus.isVerified}, shouldStopPolling=${domainStatus.shouldStopPolling}, expiresInSeconds=${domainStatus.expiresInSeconds}")
                        Resource.Success(domainStatus)
                    } ?: run {
                        Napier.d("AUTH DEBUG: Warning: wrapper.data is null, returning ErrorOther")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (HTTP_SERVER_ERROR)")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (unknown error code: ${response.resultCode})")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.d("AUTH DEBUG: Exception in checkVerificationStatus: ${e.message}")
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}


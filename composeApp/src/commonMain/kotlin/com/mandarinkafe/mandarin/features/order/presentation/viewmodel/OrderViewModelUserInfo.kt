package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.UserInfo
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForDomain
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

internal class OrderViewModelUserInfo(
    private val scope: CoroutineScope,
    private val userInfoRepository: UserInfoRepository,
    private val authRepository: AuthRepository,
    private val getState: () -> OrderState,
    private val setState: (OrderState.() -> OrderState) -> Unit,
    private val onPhoneChangedDiscount: (String) -> Unit,
) {

    fun getSavedUserInfo() {
        scope.launch {
            val initialInfo = loadInitialUserInfo()
            processInitialUserInfo(initialInfo)

            var previousPhone: String? = initialInfo?.phone?.formatPhoneNumberForDomain()
            var isFirstLoad = initialInfo == null

            userInfoRepository.userInfo.collect { userInfo ->
                if (isFirstLoad) {
                    isFirstLoad = false
                    return@collect
                }

                userInfo?.let {
                    previousPhone = processUserInfoUpdate(it, previousPhone)
                }
            }
        }
    }

    private suspend fun loadInitialUserInfo(): UserInfo? {
        return withTimeoutOrNull(Constants.USER_DATA_WAIT_TIMEOUT) {
            userInfoRepository.userInfo.first { it != null }
        } ?: run {
            Napier.w("OrderViewModel: Timeout waiting for user info")
            null
        }
    }

    private fun processInitialUserInfo(initialInfo: UserInfo?) {
        if (initialInfo == null) return

        val nameToSet =
            calculateNameToSet(getState().userInfo.name.trim(), initialInfo.name.trim())
        val formattedPhone = initialInfo.phone.formatPhoneNumberForDomain()

        setState {
            copy(
                userInfo = this.userInfo.copy(
                    name = nameToSet,
                    phone = formattedPhone,
                ),
                savedNameIsEmpty = initialInfo.name.trim().isEmpty(),
            )
        }

        onPhoneChangedDiscount(formattedPhone)
    }

    private fun processUserInfoUpdate(
        userInfo: UserInfo,
        previousPhone: String?,
    ): String {
        val newPhone = userInfo.phone.formatPhoneNumberForDomain()
        val phoneChanged = previousPhone != newPhone
        val nameToSet = calculateNameToSet(getState().userInfo.name.trim(), userInfo.name.trim())

        setState {
            copy(
                userInfo = this.userInfo.copy(
                    name = nameToSet,
                    phone = newPhone,
                ),
                savedNameIsEmpty = userInfo.name.trim().isEmpty(),
            )
        }

        if (phoneChanged) {
            onPhoneChangedDiscount(newPhone)
        }

        return newPhone
    }

    private fun calculateNameToSet(currentName: String, savedName: String): String {
        return if (currentName.isNotEmpty() && savedName.isEmpty()) {
            currentName
        } else {
            savedName
        }
    }

    fun saveUserName() {
        scope.launch {
            val currentUserInfo = userInfoRepository.getUserInfo()
            val enteredName = getState().userInfo.name.trim()

            if (enteredName.isBlank()) {
                Napier.w("OrderViewModel: Cannot save empty name")
                return@launch
            }

            val shouldUpdateName = currentUserInfo != null && (
                    currentUserInfo.name.trim().isBlank() ||
                            currentUserInfo.name.trim() != enteredName
                    )

            if (shouldUpdateName) {
                val accessToken = authRepository.getAccessToken()
                if (accessToken != null) {
                    when (val result = userInfoRepository.updateName(accessToken, enteredName)) {
                        is Resource.Success -> {
                            Napier.d("OrderViewModel: Name saved successfully")
                        }

                        is Resource.ErrorNoInternet -> {
                            Napier.w("OrderViewModel: No internet connection, name not saved")
                        }

                        is Resource.ErrorOther -> {
                            Napier.e("OrderViewModel: Failed to save name: ${result.message}")
                        }

                        else -> {}
                    }
                } else {
                    Napier.w("OrderViewModel: No access token, can't update name")
                }
            }
        }
    }
}

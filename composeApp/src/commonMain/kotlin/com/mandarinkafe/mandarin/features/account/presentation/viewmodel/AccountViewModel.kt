package com.mandarinkafe.mandarin.features.account.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEffect
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEvent
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountState
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.DeleteAccountUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
import com.mandarinkafe.mandarin.features.auth.domain.impl.UserSessionManager
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForDomain
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AccountViewModel(
    private val getActiveSessionsUseCase: GetActiveSessionsUseCase,
    private val revokeSessionUseCase: RevokeSessionUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val authRepository: AuthRepository,
    private val userInfoRepository: UserInfoRepository,
    private val userSessionManager: UserSessionManager,
) : BaseViewModel<AccountEvent, AccountEffect, AccountState>() {

    private var saveNameJob: Job? = null

    override fun setInitialState() = AccountState()

    override fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.GetInitData -> getInitData()
            is AccountEvent.LoadSessions -> loadSessions()
            is AccountEvent.RevokeSession -> revokeSession(event.sessionId)
            is AccountEvent.Logout -> logout()
            is AccountEvent.SetName -> setName(event.query)
            is AccountEvent.SaveNameNow -> saveUserName()
            is AccountEvent.OnPhoneClick -> onPhoneClick()
            is AccountEvent.ConfirmDeleteAccount -> confirmDeleteAccount()
        }
    }

    private fun getInitData() {
        loadSessions()
        observeUserInfo()
    }

    private fun onPhoneClick() {
        sendEffect(AccountEffect.ShowMessage(MR.strings.cant_change_phone))
    }

    private fun observeUserInfo() {
        viewModelScope.launch {
            var isFirstLoad = true

            // Ждем первое не-null значение с таймаутом

            val initialInfo = withTimeoutOrNull(Constants.USER_DATA_WAIT_TIMEOUT) {
                userInfoRepository.userInfo.first { it != null }
            }

            if (initialInfo != null) {
                setState {
                    copy(
                        userInfo = this.userInfo.copy(
                            name = initialInfo.name,
                            phone = initialInfo.phone.formatPhoneNumberForDomain(),
                        ),
                    )
                }
                isFirstLoad = false
            } else {
                Napier.w("AccountViewModel: Timeout waiting for user info")
            }

            // Подписываемся на дальнейшие обновления (пропускаем первое, если уже загрузили)
            userInfoRepository.userInfo.collect { userInfo ->
                if (isFirstLoad) {
                    isFirstLoad = false
                    return@collect
                }
                userInfo?.let {
                    setState {
                        copy(
                            userInfo = this.userInfo.copy(
                                name = userInfo.name,
                                phone = userInfo.phone.formatPhoneNumberForDomain(),
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun setName(query: String) {
        val oldName = state.value.userInfo.name
        val newInfo = state.value.userInfo.copy(name = query)
        setState {
            copy(
                userInfo = newInfo,
                showNameChangeButtons = oldName != query
            )
        }
        // Отменяем предыдущее сохранение
        saveNameJob?.cancel()

        // Запускаем новое сохранение с дебаунсом
        saveNameJob = viewModelScope.launch {
            delay(SAVE_NAME_DEBOUNCE) //  дебаунс
            saveUserName(query.trim())
        }
    }

    private fun saveUserName(query: String? = null) {
        viewModelScope.launch {
            val currentUserInfo = userInfoRepository.getUserInfo()
            val enteredName = query ?: state.value.userInfo.name
            // Проверяем, что имя не пустое
            if (enteredName.trim().isBlank()) {
                sendEffect(AccountEffect.ShowMessage(MR.strings.error_name_empty))
                return@launch
            }

            // Обновляем имя на сервере, если оно было пустое или изменилось
            if (currentUserInfo != null && enteredName.trim()
                    .isNotBlank() && currentUserInfo.name != enteredName
            ) {
                // Получаем access token
                val accessToken = authRepository.getAccessToken()
                if (accessToken != null) {
                    val result = userInfoRepository.updateName(accessToken, enteredName)
                    when (result) {
                        is Resource.Success -> {
                            sendEffect(AccountEffect.ShowMessage(MR.strings.name_changed_successfully))
                            setState {
                                copy(showNameChangeButtons = false)
                            }
                        }

                        is Resource.ErrorNoInternet -> {
                            sendEffect(AccountEffect.ShowMessage(MR.strings.error_no_internet))
                        }

                        is Resource.ErrorOther -> {
                            sendEffect(AccountEffect.ShowMessage(MR.strings.error_save_name))
                        }

                        else -> {}
                    }
                } else {
                    Napier.w("AccountViewModel: No access token, can't update name")
                }
            }
        }
    }


    private fun loadSessions() {
        viewModelScope.launch {
            setLoading(true)
            setState { copy(error = null) }
            val result = getActiveSessionsUseCase()
            when (result) {
                is Resource.Success -> {
                    setState {
                        copy(
                            sessions = result.data ?: emptyList(),
                            error = null
                        )
                    }
                }

                is Resource.Loading, is Resource.Idle -> {}
                is Resource.ErrorNoInternet -> {
                    Napier.e("AccountViewModel: No internet connection")
                    setState { copy(error = UiError.NoInternet) }
                    sendEffect(AccountEffect.ShowMessage(MR.strings.error_no_internet))
                }

                else -> {
                    Napier.e("AccountViewModel: Error loading sessions: ${result.message}")
                    setState { copy(error = UiError.OtherError) }
                    sendEffect(AccountEffect.ShowMessage(MR.strings.error_load_sessions))
                }
            }

            setLoading(false)
        }
    }

    private fun revokeSession(sessionId: String) {
        viewModelScope.launch {
            setLoading(true)

            when (val result = revokeSessionUseCase(sessionId)) {
                is Resource.Success -> {
                    if (result.data == true) {
                        sendEffect(AccountEffect.ShowMessage(MR.strings.session_revoked))
                        // Перезагружаем список сессий
                        loadSessions()
                    } else {
                        sendEffect(AccountEffect.ShowMessage(MR.strings.error_revoke_session))
                    }
                }

                is Resource.Loading, is Resource.Idle -> {}
                is Resource.ErrorNoInternet -> {
                    sendEffect(AccountEffect.ShowMessage(MR.strings.error_no_internet))
                }

                else -> {
                    Napier.e("AccountViewModel: Error revoking session: ${result.message}")
                    sendEffect(AccountEffect.ShowMessage(MR.strings.error_revoke_session))
                }
            }

            setLoading(false)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                userSessionManager.logout()
                sendEffect(AccountEffect.LoggedOut)
                setState { AccountState() }
            } catch (e: Exception) {
                Napier.e("AccountViewModel: Error during logout", e)
                sendEffect(AccountEffect.ShowMessage(MR.strings.error_logout))
            }
        }
    }


    private fun confirmDeleteAccount() {
        // Сразу удаляем аккаунт после подтверждения
        deleteAccount()
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            setLoading(true)

            try {
                when (val result = deleteAccountUseCase()) {
                    is Resource.Success -> {
                        if (result.data == true) {
                            try {
                                // Выполняем логаут для очистки всех данных на устройстве
                                userSessionManager.logout()
                            } catch (e: Exception) {
                                Napier.e(
                                    "AccountViewModel: Error during logout after account deletion",
                                    e
                                )
                                // Продолжаем выполнение даже если логаут не удался
                            }

                            sendEffect(AccountEffect.ShowMessage(MR.strings.account_deleted_successfully))
                            sendEffect(AccountEffect.AccountDeleted)
                            setState { AccountState() }
                        } else {
                            Napier.w("AccountViewModel: Server returned success but data is false")
                            sendEffect(AccountEffect.ShowMessage(MR.strings.error_delete_account))
                        }
                    }

                    is Resource.Loading, is Resource.Idle -> {}

                    is Resource.ErrorNoInternet -> {
                        Napier.e("AccountViewModel: No internet connection during account deletion")
                        sendEffect(AccountEffect.ShowMessage(MR.strings.error_no_internet))
                    }

                    is Resource.ErrorOther -> {
                        Napier.e("AccountViewModel: Error deleting account: ${result.message}")
                        sendEffect(AccountEffect.ShowMessage(MR.strings.error_delete_account))
                    }

                    is Resource.ErrorEmptyData -> {
                        Napier.e("AccountViewModel: Empty data error during account deletion")
                        sendEffect(AccountEffect.ShowMessage(MR.strings.error_delete_account))
                    }
                }
            } catch (e: Exception) {
                Napier.e("AccountViewModel: Exception during account deletion", e)
                sendEffect(AccountEffect.ShowMessage(MR.strings.error_delete_account))
            } finally {
                setLoading(false)
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private companion object {
        const val SAVE_NAME_DEBOUNCE = 2000L
    }
}
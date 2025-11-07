package com.mandarinkafe.mandarin.features.account.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEffect
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEvent
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountState
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
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
    private val authRepository: AuthRepository,
    private val userInfoRepository: UserInfoRepository,
) : BaseViewModel<AccountEvent, AccountEffect, AccountState>() {

    private var saveNameJob: Job? = null

    override fun setInitialState() = AccountState()

    init {
        observeUserInfo()
        loadSessions()
    }

    override fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.LoadSessions -> loadSessions()
            is AccountEvent.RevokeSession -> revokeSession(event.sessionId)
            is AccountEvent.Logout -> logout()
            is AccountEvent.SetName -> setName(event.query)
            is AccountEvent.SaveNameNow -> saveUserName()
            is AccountEvent.OnPhoneClick -> onPhoneClick()
        }
    }

    private fun onPhoneClick() {
        sendEffect(AccountEffect.ShowMessage(MR.strings.cant_change_phone))
    }

    private fun observeUserInfo() {
        viewModelScope.launch {
            // Если пользователь авторизован, ждем загрузки данных
            if (authRepository.isAuthorized()) {
                Napier.d("AccountViewModel: User is authorized, waiting for user info...")
                
                // Ждем первое не-null значение (с таймаутом 5 секунд)
                val initialInfo = withTimeoutOrNull(5000) {
                    userInfoRepository.userInfo.first { it != null }
                }
                
                if (initialInfo != null) {
                    Napier.d("AccountViewModel: Initial user info loaded - name: ${initialInfo.name}, phone: ${initialInfo.phone}")
                    setState {
                        copy(
                            userInfo = this.userInfo.copy(
                                name = initialInfo.name,
                                phone = initialInfo.phone.formatPhoneNumberForDomain(),
                            ),
                        )
                    }
                } else {
                    Napier.w("AccountViewModel: Timeout waiting for user info")
                }
            }
            
            // Подписываемся на дальнейшие обновления
            userInfoRepository.userInfo.collect { userInfo ->
                Napier.d("AccountViewModel: UserInfo updated - name: ${userInfo?.name}, phone: ${userInfo?.phone}")
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
            delay(500) // 500ms дебаунс
            saveUserName(query.trim())
        }
    }

    private fun saveUserName(query: String? = null) {
        viewModelScope.launch {
            val currentUserInfo = userInfoRepository.getUserInfo()
            val enteredName = query ?: state.value.userInfo.name
            // Обновляем имя на сервере, если оно было пустое или изменилось
            if (currentUserInfo != null && enteredName.trim().isNotBlank()) {
                if (currentUserInfo.name != enteredName) {
                    // Получаем access token
                    val accessToken = authRepository.getAccessToken()
                    if (accessToken != null) {
                        Napier.d("AccountViewModel: Saving name to server: '$enteredName'")
                        val result = userInfoRepository.updateName(accessToken, enteredName)
                        when (result) {
                            is Resource.Success -> {
                                Napier.d("AccountViewModel: Name saved successfully")
                            }

                            is Resource.ErrorNoInternet -> {
                                Napier.w("AccountViewModel: No internet, name not saved")
                                sendEffect(AccountEffect.ShowMessage(MR.strings.error_no_internet))
                            }

                            is Resource.ErrorOther -> {
                                Napier.e("AccountViewModel: Failed to save name: ${result.message}")
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
    }


    private fun loadSessions() {
        viewModelScope.launch {
            setLoading(true)
            setState { copy(error = null) }
            val result = getActiveSessionsUseCase()
            when (result) {
                is Resource.Success -> {
                    Napier.d("AccountViewModel: Sessions loaded successfully: ${result.data?.size}")
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
                authRepository.logout()
                sendEffect(AccountEffect.LoggedOut)
                setState { AccountState() }
            } catch (e: Exception) {
                Napier.e("AccountViewModel: Error during logout", e)
                sendEffect(AccountEffect.ShowMessage(MR.strings.error_logout))
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}
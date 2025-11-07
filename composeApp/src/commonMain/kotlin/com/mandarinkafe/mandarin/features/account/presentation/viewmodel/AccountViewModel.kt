package com.mandarinkafe.mandarin.features.account.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEffect
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountEvent
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountContract.AccountState
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class AccountViewModel(
    private val getActiveSessionsUseCase: GetActiveSessionsUseCase,
    private val revokeSessionUseCase: RevokeSessionUseCase,
    private val authRepository: AuthRepository,
) : BaseViewModel<AccountEvent, AccountEffect, AccountState>() {

    override fun setInitialState() = AccountState()

    init {
        loadSessions()
    }

    override fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.LoadSessions -> loadSessions()
            is AccountEvent.RevokeSession -> revokeSession(event.sessionId)
            is AccountEvent.Logout -> logout()
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
                    sendEffect(AccountEffect.ShowError("Нет подключения к интернету"))
                }

                else -> {
                    Napier.e("AccountViewModel: Error loading sessions: ${result.message}")
                    setState { copy(error = UiError.OtherError) }
                    sendEffect(AccountEffect.ShowError(result.message ?: "Ошибка загрузки сессий"))
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
                        Napier.d("AccountViewModel: Session revoked successfully")
                        sendEffect(AccountEffect.SessionRevoked)
                        // Перезагружаем список сессий
                        loadSessions()
                    } else {
                        Napier.e("AccountViewModel: Failed to revoke session")
                        sendEffect(AccountEffect.ShowError("Не удалось завершить сессию"))
                    }
                }

                is Resource.Loading, is Resource.Idle -> {}
                is Resource.ErrorNoInternet -> {
                    Napier.e("AccountViewModel: No internet connection")
                    sendEffect(AccountEffect.ShowError("Нет подключения к интернету"))
                }

                else -> {
                    Napier.e("AccountViewModel: Error revoking session: ${result.message}")
                    sendEffect(
                        AccountEffect.ShowError(
                            result.message ?: "Ошибка завершения сессии"
                        )
                    )
                }
            }

            setLoading(false)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                Napier.d("AccountViewModel: Logging out")
                authRepository.logout()
                sendEffect(AccountEffect.LoggedOut)
            } catch (e: Exception) {
                Napier.e("AccountViewModel: Error during logout", e)
                sendEffect(AccountEffect.ShowError("Ошибка при выходе"))
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}
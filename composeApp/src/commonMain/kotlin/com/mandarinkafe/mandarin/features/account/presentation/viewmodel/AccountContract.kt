package com.mandarinkafe.mandarin.features.account.presentation.viewmodel

import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.UserInfoUi
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface AccountContract {
    sealed interface AccountEvent : BaseContract.BaseEvent {
        data object LoadSessions : AccountEvent
        data class RevokeSession(val sessionId: String) : AccountEvent
        data object Logout : AccountEvent
        data class SetName(val query: String) : AccountEvent
    }

    sealed interface AccountEffect : BaseContract.BaseEffect {
        data object SessionRevoked : AccountEffect
        data object LoggedOut : AccountEffect
        data class ShowError(val message: String) : AccountEffect
    }

    data class AccountState(
        val userInfo: UserInfoUi = UserInfoUi(),
        val isLoading: Boolean = false,
        val sessions: List<ActiveSession> = emptyList(),
        val error: UiError? = null,
    ) : BaseContract.BaseState
}
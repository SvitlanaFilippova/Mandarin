package com.mandarinkafe.mandarin.features.auth.presentation.viewmodel

import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.presentation.models.PhoneVerificationDataUi
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface AuthContract {
    sealed interface AuthEvent : BaseContract.BaseEvent {
        data object RequestAuth : AuthEvent
        data class SetPhone(val query: String) : AuthEvent
        data object ForceRefresh : AuthEvent
        data object AskSmsCode : AuthEvent
        data class SetCodeQuery(val query: String) : AuthEvent
        data object CodeEntered : AuthEvent
    }

    sealed interface AuthEffect : BaseContract.BaseEffect

    data class AuthState(
        val isLoading: Boolean = false,
        val error: UiError? = null,
        val phoneQuery: String = "",
        val phoneVerificationData: PhoneVerificationDataUi? = null,
        val smsCodeQuery: String = "",
    ) : BaseContract.BaseState {
        val isPhoneValid: Boolean
            get() =
                phoneQuery.length == Constants.VALID_PHONE_LENGTH

    }
}
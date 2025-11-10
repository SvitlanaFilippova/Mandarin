package com.mandarinkafe.mandarin.features.auth.presentation.viewmodel

import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.presentation.models.PhoneVerificationDataUi
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface AuthContract {
    sealed interface AuthEvent : BaseContract.BaseEvent {
        // Звонок
        data class SetPhone(val query: String) : AuthEvent
        data object RequestAuth : AuthEvent
        data object ForceRefresh : AuthEvent

        // SMS
        data object AskSmsCode : AuthEvent
        data class SetCodeQuery(val query: String) : AuthEvent
        data object CodeEntered : AuthEvent

    }

    sealed interface AuthEffect : BaseContract.BaseEffect {
        data object SuccessAuth : AuthEffect
        data object SuccessAuthWithCartChanged : AuthEffect
    }

    data class AuthState(
        val isLoading: Boolean = false,
        val error: UiError? = null,
        val phoneQuery: String = "",
        val phoneVerificationData: PhoneVerificationDataUi? = null,
        val remainingTimeToCall: Int? = null,
        val remainingTimeToResendSms: Int? = null,
        val smsCodeQuery: String = "",
        val smsValidationError: StringResource?? = null,
        val smsCheckError: Boolean = false,
        val activeVerificationPhone: String? = null, // Номер, для которого активна сессия верификации

    ) : BaseContract.BaseState {
        val isPhoneValid: Boolean
            get() =
                phoneQuery.length == Constants.VALID_PHONE_LENGTH
    }
}
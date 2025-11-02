package com.mandarinkafe.mandarin.features.auth.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.domain.api.CheckVerificationStatusUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.presentation.models.toUi
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract.AuthEffect
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract.AuthEvent
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract.AuthState
import com.mandarinkafe.mandarin.util.Constants.SMS_CODE_LENGTH
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForDomain
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class AuthViewModel(
    private val requestPhoneVerification: RequestPhoneVerificationUseCase,
    private val checkVerificationStatus: CheckVerificationStatusUseCase,
) : BaseViewModel<AuthEvent, AuthEffect, AuthState>() {
    override fun setInitialState() = AuthState()

    override fun onEvent(event: AuthEvent) {
        Napier.d("AUTH DEBUG: Received event")
        when (event) {
            is AuthEvent.ForceRefresh -> forceRefresh()
            is AuthEvent.RequestAuth -> requestAuth()
            is AuthEvent.SetPhone -> setPhone(rawPhone = event.query)
            is AuthEvent.AskSmsCode -> askSms()
            is AuthEvent.CodeEntered -> {}
            is AuthEvent.SetCodeQuery -> setCodeQuery(event.query)
        }
    }

    private fun forceRefresh() {
        // TODO()
    }

    private fun askSms() {
        // реализоваь тут вызов смс-авторизации и передать в ЮИ сигнал о необходимости показать поле для ввода
    }

    private fun setCodeQuery(query: String) {
        val clearedCode = query.filter { it.isDigit() }.take(SMS_CODE_LENGTH)
        setState { copy(smsCodeQuery = clearedCode) }

    }

    private fun requestAuth() {
        viewModelScope.launch {
            val phone = state.value.phoneQuery
            Napier.d("AUTH DEBUG: Requesting phone verification for phone: $phone")
            val response: Resource<PhoneVerificationData> =
                requestPhoneVerification(
                    phone = phone
                )
            Napier.d("AUTH DEBUG: Phone verification response received")
            when (response) {
                is Resource.Success -> {
                    Napier.d("AUTH DEBUG: Verification request successful, data: ${response.data}")
                    proceedSuccessAuthRequest(data = response.data, userPhone = phone)
                }

                else -> {
                    Napier.d("AUTH DEBUG: Verification request error")
                    setError(response)
                }
            }
        }
    }

    private fun proceedSuccessAuthRequest(data: PhoneVerificationData?, userPhone: String) {
        data?.let {
            Napier.d("AUTH DEBUG: Processing successful auth request, checkId: ${it.checkId}, callPhone: ${it.phoneToCall}")
            setState {
                copy(
                    phoneVerificationData = data.toUi(userPhone = userPhone.formatPhoneNumberForUi())
                )
            }
        } ?: run {
            Napier.d("AUTH DEBUG: Warning: proceedSuccessAuthRequest called with null data")
        }
    }

    private fun setError(response: Resource<*>) {
        Napier.d("AUTH DEBUG: Setting error state")
        when (response) {
            is Resource.ErrorNoInternet -> {
                Napier.d("AUTH DEBUG: Error: No internet connection")
            }

            is Resource.ErrorOther -> {
                Napier.d("AUTH DEBUG: Error: ${response.message}")
            }

            is Resource.ErrorEmptyData -> {
                Napier.d("AUTH DEBUG: Error: Empty data received")
            }

            else -> {
                Napier.d("AUTH DEBUG: Error: Unknown error type")
            }
        }
        setState { copy(error = UiError.OtherError) }
//        when (response) {
//            is Resource.ErrorEmptyData<*> -> TODO()
//            is Resource.ErrorNoInternet<*> -> TODO()
//            is Resource.ErrorOther<*> ->  set
//            else -> return
//        }
    }

    private fun setPhone(rawPhone: String) {
        setState { copy(phoneQuery = rawPhone.formatPhoneNumberForDomain()) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading, error = null) }
    }
}
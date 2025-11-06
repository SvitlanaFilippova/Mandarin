package com.mandarinkafe.mandarin.features.auth.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestSmsVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.VerificationStatusInteractor
import com.mandarinkafe.mandarin.features.auth.domain.impl.UserSessionManager
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.features.auth.presentation.models.toUi
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract.AuthEffect
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract.AuthEvent
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract.AuthState
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.SMS_CODE_LENGTH
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.formatPhoneNumberForDomain
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
    private val requestPhoneVerification: RequestPhoneVerificationUseCase,
    private val statusInteractor: VerificationStatusInteractor,
    private val requestSmsVerification: RequestSmsVerificationUseCase,
    private val userSessionManager: UserSessionManager,
) : BaseViewModel<AuthEvent, AuthEffect, AuthState>() {

    private var callTimerJob: Job? = null
    private var smsTimerJob: Job? = null
    private var statusPollingJob: Job? = null
    override fun setInitialState() = AuthState()

    override fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.RequestAuth -> requestAuth()
            is AuthEvent.SetPhone -> setPhone(rawPhone = event.query)
            is AuthEvent.AskSmsCode -> sendAuthRequestBySms()
            is AuthEvent.CodeEntered -> checkIfCodeIsValid()
            is AuthEvent.SetCodeQuery -> setCodeQuery(event.query)
            is AuthEvent.ForceRefresh -> forceRefresh()
        }
    }

    private fun forceRefresh() {
        viewModelScope.launch {
            val checkId = state.value.phoneVerificationData?.checkId
            checkId?.let {
                val response = statusInteractor.checkByCheckId(checkId)
                proceedAuthStatusResponse(response)
            }
        }
    }

    private fun checkIfCodeIsValid() {
        viewModelScope.launch {
            setLoading()
            val code = state.value.smsCodeQuery
            val phone = state.value.phoneQuery
            val response = statusInteractor.checkSms(phone = phone, code = code)
            setLoading(false)
            proceedSmsAuthStatusResponse(response)
        }
    }

    private fun sendAuthRequestBySms() {
        val phone = state.value.phoneQuery
        setState { copy(activeVerificationPhone = phone) }
        startSmsTimer()
        viewModelScope.launch {
            requestSmsVerification.invoke(phone = phone)
        }
    }

    private fun setCodeQuery(query: String) {
        val clearedCode = query.filter { it.isDigit() }.take(SMS_CODE_LENGTH)
        setState { copy(smsCodeQuery = clearedCode, smsCheckError = false) }
    }

    private fun requestAuth() {
        with(state.value) {
            val currentPhone = phoneQuery
            val activePhone = activeVerificationPhone
            val remainingSms = remainingTimeToResendSms
            val remainingCall = remainingTimeToCall

            // Проверяем, есть ли уже активный запрос для этого номера
            if (activePhone == currentPhone) {
                val activeTimer = remainingSms ?: remainingCall
                if (activeTimer != null && activeTimer > 0) {
                    return
                }
            }
            sendAuthRequestByPhone()
        }
    }

    private fun sendAuthRequestByPhone() {
        val phone = state.value.phoneQuery
        setState { copy(activeVerificationPhone = phone, smsValidationError = null, error = null) }
        viewModelScope.launch {
            val response: Resource<PhoneVerificationData> =
                requestPhoneVerification(
                    phone = phone
                )
            when (response) {
                is Resource.Success -> {
                    proceedSuccessAuthRequest(data = response.data, userPhone = phone)
                    startCallTimer(response.data?.expiresInSeconds)
                    startObservingAuthByPhoneStatus()
                }

                else -> {
                    setError(response)
                }
            }
        }
    }

    private fun proceedSuccessAuthRequest(data: PhoneVerificationData?, userPhone: String) {
        data?.let {
            setState {
                copy(
                    phoneVerificationData = data.toUi(userPhone = userPhone.formatPhoneNumberForUi())
                )
            }
        }
    }

    private fun setError(response: Resource<*>) {
        when (response) {
            is Resource.ErrorNoInternet -> {
                setState { copy(error = UiError.NoInternet) }
            }

            else -> {
                setState { copy(error = UiError.OtherError) }
            }
        }
    }

    private fun setPhone(rawPhone: String) {
        setState { copy(phoneQuery = rawPhone.formatPhoneNumberForDomain()) }
    }

    private fun startCallTimer(expiresInSeconds: Int?) {
        // Отменяем предыдущий таймер, если он был
        callTimerJob?.cancel()
        val remainingTimeToCall = expiresInSeconds ?: Constants.SECONDS_TO_CALL_DEFAULT

        // Сбрасываем время
        setState { copy(remainingTimeToCall = remainingTimeToCall) }

        // Запускаем новый таймер
        callTimerJob = viewModelScope.launch {
            while (true) {
                val remaining = state.value.remainingTimeToCall ?: break
                if (remaining <= 0) break

                delay(Constants.DELAY_1_SECOND)
                setState { copy(remainingTimeToCall = remaining - 1) }
            }
            // Когда время истекло, сбрасываем всё
            stopCallTimer()
        }
    }

    private fun stopCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = null
        setState {
            copy(
                phoneVerificationData = null,
                remainingTimeToCall = null,
                activeVerificationPhone = null
            )
        }
    }

    private fun startSmsTimer() {
        // Отменяем предыдущий таймер, если он был
        smsTimerJob?.cancel()

        // Сбрасываем время до 1 минуты
        setState { copy(remainingTimeToResendSms = Constants.SECONDS_TO_RESEND_SMS_DEFAULT) }

        // Запускаем новый таймер
        smsTimerJob = viewModelScope.launch {
            while (true) {
                val remaining = state.value.remainingTimeToResendSms ?: break
                if (remaining <= 0) break

                delay(Constants.DELAY_1_SECOND)
                setState { copy(remainingTimeToResendSms = remaining - 1) }
            }
            // Когда время истекло, сбрасываем всё
            stopSmsTimer()
        }
    }

    private fun stopSmsTimer() {
        smsTimerJob?.cancel()
        smsTimerJob = null
        setState {
            copy(
                remainingTimeToResendSms = null,
                activeVerificationPhone = null
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading, error = null, smsValidationError = null) }
    }

    private fun startObservingAuthByPhoneStatus() {
        // Отменяем предыдущий пулинг, если он был
        statusPollingJob?.cancel()
        statusPollingJob = null

        val phone = state.value.phoneQuery
        if (phone.isEmpty()) {
            return
        }

        statusPollingJob = viewModelScope.launch {
            statusInteractor.observeStatusByPhone(phone).collect { response ->
                proceedAuthStatusResponse(response)
            }
        }
    }

    private fun proceedAuthStatusResponse(response: Resource<PhoneVerificationStatus>) {
        when (response) {
            is Resource.Success -> {
                val status = response.data

                if (status == null) {
                    setState { copy(error = UiError.OtherError) }
                } else {
                    if (status.isVerified == true) {
                        // Верификация успешна - останавливаем пулинг и таймеры
                        stopStatusPolling()
                        stopCallTimer()
                        setState { copy(activeVerificationPhone = null, error = null) }
                        proceedSuccessAuth(tokens = status.tokens)
                    }

                    if (status.shouldStopPolling == true && status.isVerified != true) {
                        stopStatusPolling()
                        stopCallTimer()
                        setState { copy(error = UiError.OtherError) }
                    }
                }
            }

            is Resource.Idle, is Resource.Loading -> {
                // Игнорируем
            }

            else -> setError(response)
        }
    }

    private fun proceedSmsAuthStatusResponse(response: Resource<VerifySmsCodeResult>) {
        when (response) {
            is Resource.Success -> {
                val status = response.data
                if (status == null) {
                    setState { copy(error = UiError.OtherError) }
                } else {
                    if (status.isVerified) {
                        // Верификация успешна - останавливаем таймеры
                        stopSmsTimer()
                        setState {
                            copy(
                                smsValidationError = null,
                                smsCodeQuery = "",
                                activeVerificationPhone = null,
                                error = null
                            )
                        }
                        proceedSuccessAuth(tokens = status.tokens)

                    } else {
                        setState { copy(smsValidationError = status.reason, smsCodeQuery = "") }
                    }
                }
            }

            is Resource.Idle, is Resource.Loading -> {
                // Игнорируем
            }

            else -> setError(response)
        }
    }

    private fun proceedSuccessAuth(tokens: AuthTokens?) {
        sendEffect(AuthEffect.SuccessAuth)
        viewModelScope.launch {
        userSessionManager.onUserAuthorized(tokens)
        }
    }

    private fun stopStatusPolling() {
        statusPollingJob?.cancel()
        statusPollingJob = null
    }
}
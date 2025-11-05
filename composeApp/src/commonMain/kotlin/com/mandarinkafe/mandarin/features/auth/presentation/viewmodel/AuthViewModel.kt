package com.mandarinkafe.mandarin.features.auth.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestSmsVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.VerificationStatusInteractor
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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
    private val requestPhoneVerification: RequestPhoneVerificationUseCase,
    private val statusInteractor: VerificationStatusInteractor,
    private val requestSmsVerification: RequestSmsVerificationUseCase,
) : BaseViewModel<AuthEvent, AuthEffect, AuthState>() {

    private var callTimerJob: Job? = null
    private var smsTimerJob: Job? = null
    private var statusPollingJob: Job? = null
    override fun setInitialState() = AuthState()

    override fun onEvent(event: AuthEvent) {
        Napier.d("AUTH DEBUG: Received event: $event")
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
        val currentPhone = state.value.phoneQuery
        val activePhone = state.value.activeVerificationPhone
        val remainingSms = state.value.remainingTimeToResendSms
        val remainingCall = state.value.remainingTimeToCall

        // Проверяем, есть ли уже активный запрос для этого номера
        if (activePhone == currentPhone) {
            val activeTimer = remainingSms ?: remainingCall
            if (activeTimer != null && activeTimer > 0) {
                Napier.d("AUTH DEBUG: Request already active for phone: $currentPhone, remaining: $activeTimer seconds")
                sendEffect(AuthEffect.RequestAlreadyActive(activeTimer))
                return
            }
        }
        sendAuthRequestByPhone() // Phone
    }

    private fun sendAuthRequestByPhone() {
        val phone = state.value.phoneQuery
        setState { copy(activeVerificationPhone = phone) }
        viewModelScope.launch {
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
                    startCallTimer()
                    startObservingAuthByPhoneStatus()
                }

                else -> {
                    Napier.d("AUTH DEBUG: Verification request is NOT Success")
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

    private fun startCallTimer() {
        // Отменяем предыдущий таймер, если он был
        callTimerJob?.cancel()

        // Сбрасываем время на 5 минут
        setState { copy(remainingTimeToCall = Constants.SECONDS_TO_CALL_DEFAULT) }

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
            Napier.d("AUTH DEBUG: Cannot start polling: phone is empty")
            return
        }

        Napier.d("AUTH DEBUG: Starting auth status polling for phone: $phone")

        statusPollingJob = viewModelScope.launch {
            statusInteractor.observeStatusByPhone(phone).collect { response ->
                Napier.d("AUTH DEBUG: Received status update from Flow")
                proceedAuthStatusResponse(response)

            }

            Napier.d("AUTH DEBUG: Status polling Flow completed")
        }
    }

    private fun proceedAuthStatusResponse(response: Resource<PhoneVerificationStatus>) {
        Napier.d("AUTH DEBUG: Starting proceedAuthStatusResponse")
        when (response) {
            is Resource.Success -> {
                val status = response.data

                if (status == null) {
                    Napier.d("AUTH DEBUG: Status data is null")
                    setState { copy(error = UiError.OtherError) }
                } else {
                    Napier.d(
                        "AUTH DEBUG: Status check successful - isVerified: ${status.isVerified}, " +
                                "shouldStopPolling: ${status.shouldStopPolling}, " +
                                "expiresInSeconds: ${status.expiresInSeconds}"
                    )

                    if (status.isVerified == true) {
                        Napier.d("AUTH DEBUG: Phone verification completed successfully!")
                        // Верификация успешна - останавливаем пулинг и таймеры
                        stopStatusPolling()
                        stopCallTimer()
                        setState { copy(activeVerificationPhone = null, error = null) }
                        sendEffect(AuthEffect.SuccessAuth)
                    }

                    if (status.shouldStopPolling == true && status.isVerified != true) {
                        Napier.d("AUTH DEBUG: Should stop polling (verification expired or failed)")
                        stopStatusPolling()
                        stopCallTimer()
                        setState { copy(error = UiError.OtherError) }
                    }
                }
            }

            is Resource.Idle, is Resource.Loading -> {
                Napier.d("AUTH DEBUG: Idle state")
            }

            else -> setError(response)
        }
    }

    private fun proceedSmsAuthStatusResponse(response: Resource<VerifySmsCodeResult>) {
        Napier.d("SMS AUTH DEBUG: Starting proceedAuthStatusResponse")
        when (response) {
            is Resource.Success -> {
                val status = response.data
                if (status == null) {
                    Napier.d("SMS AUTH DEBUG: Status data is null")
                    setState { copy(error = UiError.OtherError) }
                } else {
                    Napier.d(
                        "SMS AUTH DEBUG: Status check successful - isVerified: ${status.isVerified}, "
                    )

                    if (status.isVerified) {
                        Napier.d("SMS AUTH DEBUG: Phone verification completed successfully!")
                        // Верификация успешна - останавливаем пулинг и таймеры
                        stopSmsTimer()
                        setState {
                            copy(
                                smsValidationError = null,
                                smsCodeQuery = "",
                                activeVerificationPhone = null,
                                error = null
                            )
                        }
                        sendEffect(AuthEffect.SuccessAuth)
                    } else {
                        setState { copy(smsValidationError = status.reason, smsCodeQuery = "") }
                    }
                }
            }


            is Resource.Idle, is Resource.Loading -> {
                Napier.d("AUTH DEBUG: Idle state")
            }

            else -> setError(response)
        }
    }

    private fun stopStatusPolling() {
        Napier.d("AUTH DEBUG: Stopping status polling")
        statusPollingJob?.cancel()
        statusPollingJob = null
    }
}
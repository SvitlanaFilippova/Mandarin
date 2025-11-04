package com.mandarinkafe.mandarin.features.auth.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.VerificationStatusInteractor
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
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
        // TODO()
    }

    private fun sendAuthRequestBySms() {
        // TODO()
        // реализовать тут вызов смс-авторизации и передать в ЮИ сигнал о необходимости показать поле для ввода
        startSmsTimer()
    }

    private fun setCodeQuery(query: String) {
        val clearedCode = query.filter { it.isDigit() }.take(SMS_CODE_LENGTH)
        setState { copy(smsCodeQuery = clearedCode, smsCheckError = false) }

    }

    private fun requestAuth() {
        // тут проверять, есть ли активные запросы авторизации сначала, потом решать, что показать
//        sendAuthRequestBySms() // SMS
        sendAuthRequestByPhone() // Phone
    }

    private fun sendAuthRequestByPhone() {
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
                remainingTimeToCall = null
            )
        }
    }

    private fun startSmsTimer() {
        // Отменяем предыдущий таймер, если он был
        smsTimerJob?.cancel()

        // Сбрасываем время на 5 минут
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
                remainingTimeToResendSms = null
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading, error = null) }
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
                        setState { copy(isVerified = true) }
                        sendEffect(AuthEffect.SuccessAuth) // TODO добавить обработку эффекта для редиректа юзера дальше
                    }

                    if (status.shouldStopPolling == true && status.isVerified != true) {
                        Napier.d("AUTH DEBUG: Should stop polling (verification expired or failed)")
                        stopStatusPolling()
                        stopCallTimer()
                        setState { copy(error = UiError.OtherError) }
                    }
                }
            }

            is Resource.ErrorNoInternet -> {
                Napier.d("AUTH DEBUG: Status check failed: No internet connection")
            }

            is Resource.ErrorOther -> {
                Napier.d("AUTH DEBUG: Status check failed: ${response.message}")
            }

            is Resource.ErrorEmptyData -> {
                Napier.d("AUTH DEBUG: Status check failed: Empty data received")
            }

            is Resource.Idle -> {
                Napier.d("AUTH DEBUG: Idle state")
            }

            is Resource.Loading -> {
                Napier.d("AUTH DEBUG: Loading state")
            }
        }
    }

    private fun stopStatusPolling() {
        Napier.d("AUTH DEBUG: Stopping status polling")
        statusPollingJob?.cancel()
        statusPollingJob = null
    }
}
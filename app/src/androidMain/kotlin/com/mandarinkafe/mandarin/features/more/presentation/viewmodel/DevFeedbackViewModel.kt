package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.more.domain.api.DevFeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackContract.DevFeedbackEffect
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackContract.DevFeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackContract.DevFeedbackState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.Result
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DevFeedbackViewModel(
    private val repository: DevFeedbackRepository
) :
    BaseViewModel<DevFeedbackEvent, DevFeedbackEffect, DevFeedbackState>() {
    override fun setInitialState() = DevFeedbackState()

    override fun onEvent(event: DevFeedbackEvent) {
        when (event) {
            is DevFeedbackEvent.SetEmail -> setState { copy(email = event.query) }
            is DevFeedbackEvent.SetMessage -> setState { copy(message = event.query) }
            is DevFeedbackEvent.SetName -> setState { copy(name = event.query) }
            is DevFeedbackEvent.SetNeedFeedback -> setState { copy(needAnswer = event.flag) }
            is DevFeedbackEvent.SetPhone -> setPhone(event.query)
            is DevFeedbackEvent.SubmitForm -> submitForm()
        }
    }

    private fun submitForm() = viewModelScope.launch {
        val feedback =
            with(state.value) {
                Feedback(
                    name = name,
                    phone = phone,
                    email = email,
                    message = message,
                    needAnswer = needAnswer
                )
            }
        setLoading()

        val result = repository.sendDevFeedback(feedback)

        setLoading(false)

        when (result) {
            is Result.Success -> {
                proceedSuccessSubmitForm()
            }

            is Result.Failure -> {
                sendEffect(DevFeedbackEffect.ShowError(result.throwable.message ?: "Ошибка"))
            }
        }
    }

    private fun proceedSuccessSubmitForm() {
        viewModelScope.launch {
            sendEffect(DevFeedbackEffect.ShowSuccess)
            delay(DELAY_FOR_FORM_RESET_AFTER_SUBMIT)
            setState { DevFeedbackState() }
        }
    }

    private fun setPhone(rawPhone: String) {
        val digitsOnly = rawPhone.filter { it.isDigit() }
        val normalized = when {
            digitsOnly.startsWith("7") -> digitsOnly.drop(1)
            digitsOnly.startsWith("8") -> digitsOnly.drop(1)
            else -> digitsOnly
        }
        val phone = normalized.take(VALID_PHONE_LENGTH)

        setState { copy(phone = phone) }

    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private companion object {
        const val DELAY_FOR_FORM_RESET_AFTER_SUBMIT = 5000L
    }
}
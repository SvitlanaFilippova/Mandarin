package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEffect
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.Result
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val repository: FeedbackRepository
) :
    BaseViewModel<FeedbackEvent, FeedbackEffect, FeedbackState>() {
    override fun setInitialState() = FeedbackState()

    override fun onEvent(event: FeedbackEvent) {
        when (event) {
            is FeedbackEvent.SetEmail -> setState { copy(email = event.query) }
            is FeedbackEvent.SetMessage -> setState { copy(message = event.query) }
            is FeedbackEvent.SetName -> setState { copy(name = event.query) }
            is FeedbackEvent.SetNeedFeedback -> setState { copy(needAnswer = event.flag) }
            is FeedbackEvent.SetPhone -> setPhone(event.query)
            is FeedbackEvent.SubmitForm -> submitForm()
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

        val result = repository.sendFeedback(feedback)

        setLoading(false)

        when (result) {
            is Result.Success -> {
                proceedSuccessSubmitForm()
            }

            is Result.Failure -> {
                sendEffect(FeedbackEffect.ShowError(result.throwable.message ?: "Ошибка"))
            }
        }
    }

    private fun proceedSuccessSubmitForm() {
        viewModelScope.launch {
            sendEffect(FeedbackEffect.ShowSuccess)
            delay(DELAY_FOR_FORM_RESET_AFTER_SUBMIT)
            setState { FeedbackState() } // обнуляем введённые ранее данные
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
package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEffect
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackState
import com.mandarinkafe.mandarin.util.Result
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val repository: FeedbackRepository,
) :
    BaseViewModel<FeedbackEvent, FeedbackEffect, FeedbackState>() {
    override fun setInitialState() = FeedbackState()

    override fun onEvent(event: FeedbackEvent) {
        when (event) {
            is FeedbackEvent.SetMessage -> setState { copy(message = event.query) }
            is FeedbackEvent.SetNeedFeedback -> setState { copy(needAnswer = event.flag) }
            is FeedbackEvent.SubmitForm -> submitForm()
        }
    }

    private fun submitForm() = viewModelScope.launch {
        val feedback =
            with(state.value) {
                Feedback(
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

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private companion object {
        const val DELAY_FOR_FORM_RESET_AFTER_SUBMIT = 5000L
    }
}

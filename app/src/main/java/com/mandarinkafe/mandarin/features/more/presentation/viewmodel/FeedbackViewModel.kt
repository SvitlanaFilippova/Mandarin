package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEffect
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor() :
    BaseViewModel<FeedbackEvent, FeedbackEffect, FeedbackState>() {
    override fun setInitialState() = FeedbackState()

    override fun onEvent(event: FeedbackEvent) {
        when (event) {
            is FeedbackEvent.SetEmail -> setState { copy(email = event.query) }
            is FeedbackEvent.SetMessage -> setState { copy(message = event.query) }
            is FeedbackEvent.SetName -> setState { copy(name = event.query) }
            is FeedbackEvent.SetNeedFeedback -> setState { copy(needFeedback = event.flag) }
            is FeedbackEvent.SetPhone -> setPhone(event.query)
            is FeedbackEvent.SubmitForm -> submitForm()
        }
    }

    private fun submitForm() {
        TODO("Not yet implemented")
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
        // не применимо
    }
}
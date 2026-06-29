package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface FeedbackContract {
    sealed interface FeedbackEvent : BaseContract.BaseEvent {
        data class SetMessage(val query: String) : FeedbackEvent
        data class SetNeedFeedback(val flag: Boolean) : FeedbackEvent
        data object SubmitForm : FeedbackEvent
    }

    sealed interface FeedbackEffect : BaseContract.BaseEffect {
        data object ShowSuccess : FeedbackEffect
        data class ShowError(val message: String) : FeedbackEffect
    }

    data class FeedbackState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val message: String = "",
        val needAnswer: Boolean = false,
    ) : BaseContract.BaseState {
        val isMessageValid: Boolean
            get() = message.isNotBlank()

        val isFormValid: Boolean
            get() = isMessageValid
    }
}

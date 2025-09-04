package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface FeedbackContract {
    sealed interface FeedbackEvent : BaseEvent {
        data class SetPhone(val query: String) : FeedbackEvent
        data class SetName(val query: String) : FeedbackEvent
        data class SetEmail(val query: String) : FeedbackEvent
        data class SetMessage(val query: String) : FeedbackEvent
        data class SetNeedFeedback(val flag: Boolean) : FeedbackEvent
        data object SubmitForm : FeedbackEvent
    }

    sealed interface FeedbackEffect : BaseEffect {
        data object ShowSuccess : FeedbackEffect
        data class ShowError(val message: String) : FeedbackEffect
    }

    data class FeedbackState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val name: String = "",
        val phone: String = "",
        val email: String = "",
        val message: String = "",
        val needAnswer: Boolean = false
    ) : BaseState
}
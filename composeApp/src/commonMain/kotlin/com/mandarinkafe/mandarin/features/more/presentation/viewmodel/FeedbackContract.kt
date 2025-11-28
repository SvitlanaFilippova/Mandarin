package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface FeedbackContract {
    sealed interface FeedbackEvent : BaseContract.BaseEvent {
        data class SetPhone(val query: String) : FeedbackEvent
        data class SetName(val query: String) : FeedbackEvent
        data class SetEmail(val query: String) : FeedbackEvent
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
        val name: String = "",
        val phone: String = "",
        val email: String = "",
        val message: String = "",
        val needAnswer: Boolean = false,
    ) : BaseContract.BaseState {
        val isContactValid: Boolean
            get() =
                !needAnswer || phone.length == Constants.VALID_PHONE_LENGTH || email.isNotBlank()

        // Валидация
        val isMessageValid: Boolean
            get() = message.isNotBlank()

        val isFormValid: Boolean
            get() = isMessageValid && isContactValid
    }
}


package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.util.presentation.BaseEffect
import com.mandarinkafe.mandarin.util.presentation.BaseEvent
import com.mandarinkafe.mandarin.util.presentation.BaseState
import com.mandarinkafe.mandarin.util.Constants

sealed interface DevFeedbackContract {
    sealed interface DevFeedbackEvent : BaseEvent {
        data class SetPhone(val query: String) : DevFeedbackEvent
        data class SetName(val query: String) : DevFeedbackEvent
        data class SetEmail(val query: String) : DevFeedbackEvent
        data class SetMessage(val query: String) : DevFeedbackEvent
        data class SetNeedFeedback(val flag: Boolean) : DevFeedbackEvent
        data object SubmitForm : DevFeedbackEvent
    }

    sealed interface DevFeedbackEffect : BaseEffect {
        data object ShowSuccess : DevFeedbackEffect
        data class ShowError(val message: String) : DevFeedbackEffect
    }

    data class DevFeedbackState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val name: String = "",
        val phone: String = "",
        val email: String = "",
        val message: String = "",
        val needAnswer: Boolean = false
    ) : BaseState {
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
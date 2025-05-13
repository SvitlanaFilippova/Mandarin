package com.mandarinkafe.mandarin.placeholder.ui.view_model

import com.mandarinkafe.mandarin.core.BaseEffect
import com.mandarinkafe.mandarin.core.BaseEvent
import com.mandarinkafe.mandarin.core.BaseState

sealed interface PlaceholderContract {
    sealed interface PlaceholderEvent : BaseEvent {
        data object Retry : PlaceholderEvent
        data object OnPhoneClick : PlaceholderEvent
    }

    sealed interface PlaceholderEffect : BaseEffect

    data class PlaceholderState(
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
    ) : BaseState
}

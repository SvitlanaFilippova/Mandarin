package com.mandarinkafe.mandarin.placeholder.ui.view_model

sealed interface PlaceholderContract {
    sealed interface Event {
        data object Retry : Event
        data object OnPhoneClick : Event
    }

    sealed interface Effect

    data class State(
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
    )
}
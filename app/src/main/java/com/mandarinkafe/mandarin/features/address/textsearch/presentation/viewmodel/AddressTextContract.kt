package com.mandarinkafe.mandarin.features.address.textsearch.presentation.viewmodel

import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface AddressTextContract {
    sealed interface AddressTexEvent : BaseEvent
    sealed interface AddressTexEffect : BaseEffect
    data class AddressTexState(val isLoading: Boolean = false) : BaseState
}
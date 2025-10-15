package com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.util.presentation.BaseEffect
import com.mandarinkafe.mandarin.util.presentation.BaseEvent
import com.mandarinkafe.mandarin.util.presentation.BaseState

sealed interface SavedAddressesContract {
    sealed interface SavedAddressesEvent : BaseEvent {
        data object RefreshAddresses : SavedAddressesEvent
        data object AddNewAddress : SavedAddressesEvent
        data class EditAddress(val address: Address) : SavedAddressesEvent
        data class RemoveAddress(val id: String) : SavedAddressesEvent
    }

    sealed interface SavedAddressesEffect : BaseEffect {

        data object AddNewAddress : SavedAddressesEffect
        data class EditAddress(val address: Address) : SavedAddressesEffect
        data class ShowError(val message: String) : SavedAddressesEffect
    }

    data class SavedAddressesState(
        val data: List<Address> = emptyList(),
        val isLoading: Boolean? = null
    ) : BaseState
}
package com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface SavedAddressesContract {
    sealed interface SavedAddressesEvent : BaseContract.BaseEvent {
        data object RefreshAddresses : SavedAddressesEvent
        data object AddNewAddress : SavedAddressesEvent
        data class EditAddress(val address: Address) : SavedAddressesEvent
        data class RemoveAddress(val id: String) : SavedAddressesEvent
    }

    sealed interface SavedAddressesEffect : BaseContract.BaseEffect {

        data object AddNewAddress : SavedAddressesEffect
        data class EditAddress(val address: Address) : SavedAddressesEffect
        data class ShowError(val message: String) : SavedAddressesEffect
    }

    data class SavedAddressesState(
        val data: List<Address> = emptyList(),
        val isLoading: Boolean? = null,
    ) : BaseContract.BaseState
}


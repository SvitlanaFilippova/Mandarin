package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface AddressDetailsContract {
    sealed interface AddressDetailsEvent : BaseEvent {
        data class SetAddress(val address: Address) : AddressDetailsEvent
        data object ChangeLocation : AddressDetailsEvent
        data class SetAddressType(val addressType: AddressType) : AddressDetailsEvent
        data class SetApartmentNumber(val query: String) : AddressDetailsEvent
        data class SetEntrance(val query: String) : AddressDetailsEvent
        data class SetFloor(val query: String) : AddressDetailsEvent
        data class SetIntercom(val query: String) : AddressDetailsEvent
        data class SetAddressComment(val query: String) : AddressDetailsEvent
        data object SaveAddress : AddressDetailsEvent
        data object RemoveAddress : AddressDetailsEvent
        data object OnMissingRequiredInfo : AddressDetailsEvent
    }

    sealed interface AddressDetailsEffect : BaseEffect {
        data class EditLocation(val address: Address) : AddressDetailsEffect
        data object ShowDeleteConfirmDialog : AddressDetailsEffect
    }

    data class AddressDetailState(
        val address: Address = Address(),
        val isError: Boolean = false,
    ) : BaseState {

        val addressIsValid: Boolean
            get() =
                with(address) {
                    noNeedAddressDetails ||
                            apartmentNumber.isNotEmpty() && entrance.isNotEmpty() && floor.isNotEmpty()
                }
    }
}
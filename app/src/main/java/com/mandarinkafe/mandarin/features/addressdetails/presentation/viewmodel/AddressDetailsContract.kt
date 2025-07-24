package com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface AddressDetailsContract {
    sealed interface AddressDetailsEvent : BaseEvent {
        data class SetAddress(val address: UiAddress) : AddressDetailsEvent
        data object ChangeLocation : AddressDetailsEvent
        data class IsPrivateHouseToggled(val isPrivateHouse: Boolean) : AddressDetailsEvent
        data class SetApartmentNumber(val query: String) : AddressDetailsEvent
        data class SetEntrance(val query: String) : AddressDetailsEvent
        data class SetFloor(val query: String) : AddressDetailsEvent
        data class SetIntercom(val query: String) : AddressDetailsEvent
        data class SetAddressComment(val query: String) : AddressDetailsEvent
        data object SaveAddressAsNew : AddressDetailsEvent
        data class SaveAddressAsEdited(val oldAddress: UiAddress) : AddressDetailsEvent
    }

    sealed interface AddressDetailsEffect : BaseEffect {
        data class EditLocation(val address: UiAddress) : AddressDetailsEffect
    }

    data class AddressDetailState(
        val address: UiAddress = UiAddress(),
        val isError: Boolean = false,
    ) : BaseState {

        val addressIsValid: Boolean
            get() =
                with(address) {
                    isPrivateHouse ||
                            apartmentNumber.isNotEmpty() && entrance.isNotEmpty() && floor.isNotEmpty()
                }
    }
}
package com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface AddressDetailsContract {
    sealed interface AddressDetailsEvent : BaseEvent {
        data class EditLocation(val address: UiAddress) : AddressDetailsEvent
        data class IsPrivateHouseToggled(val isPrivateHouse: Boolean) : AddressDetailsEvent
        data class SetAddress(val query: String) : AddressDetailsEvent
        data class SetAddressComment(val query: String) : AddressDetailsEvent
        data class SetApartmentNumber(val query: String) : AddressDetailsEvent
        data class SetEntrance(val query: String) : AddressDetailsEvent
        data class SetFloor(val query: String) : AddressDetailsEvent
        data class SetIntercom(val query: String) : AddressDetailsEvent
    }

    sealed interface AddressDetailsEffect : BaseEffect

    data class AddressDetailState(
        val initialAddress: UiAddress? = null,
        val address: UiAddress = UiAddress(),
    ) : BaseState {

        val apartmentDetailsIsValid: Boolean
            get() =
                with(address) {
                    isPrivateHouse ||
                            apartmentNumber.isNotEmpty() && apartmentEntrance.isNotEmpty() && apartmentFloor.isNotEmpty()
                }
    }
}
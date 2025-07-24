package com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailState
import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect.EditLocation
import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEvent
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressDetailsViewModel @Inject constructor() :
    BaseViewModel<AddressDetailsEvent, AddressDetailsEffect, AddressDetailState>() {
    override fun setInitialState() = AddressDetailState()

    override fun onEvent(event: AddressDetailsEvent) {
        when (event) {
            is AddressDetailsEvent.ChangeLocation -> changeLocation()
            is AddressDetailsEvent.IsPrivateHouseToggled -> toggleIsPrivateHouse(event.isPrivateHouse)
            is AddressDetailsEvent.SetAddress -> setAddress(event.address)
            is AddressDetailsEvent.SetApartmentNumber -> setApartmentNumber(event.query)
            is AddressDetailsEvent.SetEntrance -> setEntrance(event.query)
            is AddressDetailsEvent.SetFloor -> setFloor(event.query)
            is AddressDetailsEvent.SetIntercom -> setIntercom(event.query)
            is AddressDetailsEvent.SetAddressComment -> setAddressComment(event.query)
            is AddressDetailsEvent.SaveAddressAsEdited -> TODO()
            is AddressDetailsEvent.SaveAddressAsNew -> TODO()
        }
    }

    private fun setAddress(address: UiAddress) {
        setState {
            copy(address = address)
        }
    }

    private fun changeLocation() {
        val currentAddress = state.value.address
        sendEffect(
            EditLocation(
                address = currentAddress
            )
        )
    }

    private fun setApartmentNumber(query: String) = updateAddress { copy(apartmentNumber = query) }

    private fun setAddressComment(query: String) = updateAddress { copy(comment = query) }

    private fun setEntrance(query: String) = updateAddress { copy(entrance = query) }

    private fun setFloor(query: String) = updateAddress { copy(floor = query) }

    private fun setIntercom(query: String) = updateAddress { copy(intercom = query) }

    private fun toggleIsPrivateHouse(isPrivateHouse: Boolean) =
        updateAddress { copy(isPrivateHouse = isPrivateHouse) }

    private fun updateAddress(transform: UiAddress.() -> UiAddress) {
        setState {
            copy(address = address.transform())

        }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо
    }
}
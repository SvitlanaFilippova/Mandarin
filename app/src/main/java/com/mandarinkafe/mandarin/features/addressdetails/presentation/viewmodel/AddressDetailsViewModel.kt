package com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailState
import com.mandarinkafe.mandarin.features.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
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
            is AddressDetailsEvent.EditLocation -> TODO()
            is AddressDetailsEvent.IsPrivateHouseToggled -> toggleIsPrivateHouse(event.isPrivateHouse)
            is AddressDetailsEvent.SetAddress -> TODO()
            is AddressDetailsEvent.SetAddressComment -> TODO()
            is AddressDetailsEvent.SetApartmentNumber -> TODO()
            is AddressDetailsEvent.SetEntrance -> TODO()
            is AddressDetailsEvent.SetFloor -> TODO()
            is AddressDetailsEvent.SetIntercom -> TODO()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        TODO("Not yet implemented")
    }

    private fun setApartmentNumber(query: String) = updateAddress { copy(apartmentNumber = query) }

    private fun setAddressComment(query: String) = updateAddress { copy(addressComment = query) }

    private fun setEntrance(query: String) = updateAddress { copy(apartmentEntrance = query) }

    private fun setFloor(query: String) = updateAddress { copy(apartmentFloor = query) }

    private fun setIntercom(query: String) = updateAddress { copy(apartmentIntercom = query) }

    private fun toggleIsPrivateHouse(isPrivateHouse: Boolean) =
        updateAddress { copy(isPrivateHouse = isPrivateHouse) }

    private fun updateAddress(transform: UiAddress.() -> UiAddress) {
        setState {
            copy(address = address.transform())

        }
    }
}
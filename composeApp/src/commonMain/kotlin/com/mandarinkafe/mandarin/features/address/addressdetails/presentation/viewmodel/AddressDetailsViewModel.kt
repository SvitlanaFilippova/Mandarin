package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailState
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect.EditLocation
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEvent
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SaveAddressUseCase
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.launch

class AddressDetailsViewModel(
    private val saveAddressUseCase: SaveAddressUseCase,
    private val removeAddressUseCase: RemoveAddressUseCase
) :
    BaseViewModel<AddressDetailsEvent, AddressDetailsEffect, AddressDetailState>() {
    override fun setInitialState() = AddressDetailState()

    override fun onEvent(event: AddressDetailsEvent) {
        when (event) {
            is AddressDetailsEvent.ChangeLocation -> changeLocation()
            is AddressDetailsEvent.SetInitAddress -> setAddress(event.address)
            is AddressDetailsEvent.SetApartmentNumber -> setApartmentNumber(event.query)
            is AddressDetailsEvent.SetEntrance -> setEntrance(event.query)
            is AddressDetailsEvent.SetFloor -> setFloor(event.query)
            is AddressDetailsEvent.SetIntercom -> setIntercom(event.query)
            is AddressDetailsEvent.SetAddressComment -> setAddressComment(event.query)
            is AddressDetailsEvent.OnMissingRequiredInfo -> setError()
            is AddressDetailsEvent.SaveAddress -> saveAddress()
            is AddressDetailsEvent.RemoveAddress -> removeSavedAddress()
            is AddressDetailsEvent.SetAddressType -> setAddressType(event.addressType)
        }
    }

    private fun saveAddress() {
        viewModelScope.launch {
            saveAddressUseCase(state.value.address)
            sendEffect(AddressDetailsEffect.GoToParentScreen)
        }
    }

    private fun removeSavedAddress() {
        viewModelScope.launch {
            removeAddressUseCase(state.value.address.id)
            sendEffect(AddressDetailsEffect.GoToParentScreen)
        }
    }

    private fun setAddress(address: Address) {
        setState { copy(address = address) }
    }

    private fun setApartmentNumber(query: String) {
        setState {
            copy(
                address = address.copy(apartmentNumber = query),
                isError = false
            )
        }
    }

    private fun setEntrance(query: String) {
        setState {
            copy(
                address = address.copy(entrance = query),
                isError = false
            )
        }
    }

    private fun setFloor(query: String) {
        setState {
            copy(
                address = address.copy(floor = query),
                isError = false
            )
        }
    }

    private fun setIntercom(query: String) {
        setState {
            copy(
                address = address.copy(intercom = query),
                isError = false
            )
        }
    }

    private fun setAddressComment(query: String) {
        setState {
            copy(
                address = address.copy(comment = query),
                isError = false
            )
        }
    }

    private fun setAddressType(addressType: AddressType) {
        setState {
            copy(
                address = address.copy(addressType = addressType),
                isError = false
            )
        }
    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    private fun changeLocation() {
        sendEffect(EditLocation(state.value.address))
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isError = false) }
    }
}
